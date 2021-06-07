package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.ProjectUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService, Constant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null)
            throw new IllegalArgumentException("参数不能为空！");
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证账号
        User userOld = findUserByName(user.getUsername());
        if (userOld != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        // 验证邮箱
        userOld = findUserByEmail(user.getEmail());
        if (userOld != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户
        user.setSalt(ProjectUtil.generateUUID().substring(0, 5));
        user.setPassword(ProjectUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(ProjectUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        addUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/activation/101/code
        String url = domain + contextPath + "activation/" + user.getId()+"/"+user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    @Override
    public int activation(int userId, String code) {
        User user = findUserById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> verify(String email, String code) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        User user = findUserByEmail(email);
        if (user == null){
            map.put("emailMsg", "该邮箱不存在!");
            return map;
        }
        // 验证邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("code", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(user.getEmail(), "重置账号密码", content);
        return map;
    }

    @Override
    public int resetPassword(String email, String password) {
        User user = findUserByEmail(email);
        password = ProjectUtil.md5(password + user.getSalt());
        if (user.getPassword().equals(password)){
            return RESET_REPEAT;
        }else{
            updatePassword(user.getId(), password);
            return RESET_SUCCESS;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 验证账号
        User user = findUserByName(username);
        if (user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        // 验证密码
        password = ProjectUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(ProjectUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
//        insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
//        updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
//        QueryWrapper<LoginTicket> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("ticket", ticket);
//        return loginTicketMapper.selectOne(queryWrapper);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public User findUserById(int id) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", id);
//        return userMapper.selectOne(queryWrapper);
        User user = getCache(id);
        if (user == null){
            user = initCahce(id);
        }
        return user;
    }

    @Override
    public User findUserByName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", name);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int updateStatus(int id, int status) {
        User user = findUserById(id);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        user.setStatus(status);
        int rows = userMapper.update(user, updateWrapper);
        clearCache(id);
        return rows;
    }

    @Override
    public int updateHeader(int id, String header) {
        User user = findUserById(id);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        user.setHeaderUrl(header);
//        return userMapper.update(user, updateWrapper);
        int rows = userMapper.update(user, updateWrapper);
        clearCache(id);
        return rows;
    }

    /**
     * 更新密码
     * @param id 用户id
     * @param password 已经加密过后的密码
     * @return 是否成功
     */
    @Override
    public int updatePassword(int id, String password) {
        User user = findUserById(id);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        user.setPassword(password);
        int rows = userMapper.update(user, updateWrapper);
        clearCache(id);
        return rows;
    }


    // 1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCahce(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

//    @Override
//    public int insertLoginTicket(LoginTicket loginTicket) {
//        return loginTicketMapper.insert(loginTicket);
//    }

//    @Override
//    public LoginTicket selectByTicket(String ticket) {
//        QueryWrapper<LoginTicket> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("ticket", ticket);
//        return loginTicketMapper.selectOne(queryWrapper);
//    }

//    @Override
//    public int updateStatus(String ticket, int status) {
//        LoginTicket loginTicket = selectByTicket(ticket);
//        UpdateWrapper<LoginTicket> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("ticket", ticket);
//        updateWrapper.set("status", status);
//        return loginTicketMapper.update(loginTicket, updateWrapper);
//    }
}
