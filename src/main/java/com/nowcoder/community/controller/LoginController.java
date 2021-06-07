package com.nowcoder.community.controller;

import ch.qos.logback.core.util.TimeUtil;
import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.ProjectUtil;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class LoginController implements Constant {

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @GetMapping("/forget")
    public String getForgetPage(){
        return "/site/forget";
    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String , Object> map = userService.register(user);
        if (map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/verify")
    @ResponseBody
    public Map<String, Object> sendVerification(Model model, @RequestParam(defaultValue = "",value = "email") String email, HttpSession session){
        // 生成验证码
        String code = ProjectUtil.generateUUID().substring(0, 6);
        Map<String, Object> map = userService.verify(email, code);
        if (map == null || map.isEmpty()){
            session.setAttribute("code", code);
            session.setAttribute("email", email);
            return new HashMap<String, Object>(){
                {
                    put("email", email);
                }
            };
        }else{
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return new HashMap<String, Object>(){
                {
                    put("email", email);
                    put("emailMsg",map.get("emailMsg"));
                }
            };
        }
//        model.addAttribute("email", email);
    }

    @PostMapping("/forget")
    public String forget(String email, String password, String code,
                         Model model, HttpSession session){
        String verifyCode = (String)session.getAttribute("code");
        String emailVerified = (String)session.getAttribute("email");
        // 检查验证码
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code) || !verifyCode.equals(code)){
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/forget";
        }
        if (StringUtils.isBlank(email) || !emailVerified.equals(email)){
            model.addAttribute("emailMsg", "邮箱错误！");
            return "/site/forget";
        }
        // 重置密码
        int result = userService.resetPassword(email, password);
        if (result == RESET_SUCCESS){
            model.addAttribute("msg", "密码重置成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }else if (result == RESET_REPEAT){
            model.addAttribute("passwordMsg", "无效操作，重复设置账号密码！");
            return "/site/forget";
        }else{
            return "/site/forget";
        }
    }

    // http://localhost:8080/activation/101/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if (result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，该账号已经可以激活过了！");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        // session.setAttribute("kaptcha", text);

        // 验证码的归属
        String kaptchaOwner = ProjectUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error("响应验证码失败："+e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
                        Model model/*, HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        // String kaptcha = (String) session.getAttribute("kaptcha");
        // 检查验证码
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }
        // 检查账号，密码
        int expiredSeconds = rememberme? REMEMBER_EXPIRED_SECONDS: DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket, HttpServletRequest request){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
