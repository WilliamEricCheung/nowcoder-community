package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.AlphaDAO;
import com.nowcoder.community.util.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
@Slf4j
//@Scope("prototype") // 默认为单例模式，prototype就变成了多例
public class AlphaService {

    @Autowired
    private AlphaDAO alphaDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
       return alphaDAO.select();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(ProjectUtil.generateUUID().substring(0,5));
        user.setPassword(ProjectUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://img.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userService.addUser(user);
        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(ProjectUtil.generateUUID().substring(0,5));
                user.setPassword(ProjectUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://img.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userService.addUser(user);
                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人");
                post.setCreateTime(new Date());
                discussPostService.addDiscussPost(post);
                Integer.valueOf("abc");
                return "ok";
            }
        });
    }

    // 让该让发在多线程环境下，被异步的调用
    @Async
    public void execute1(){
        log.debug("execute");
        System.out.println("execute");
    }

    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2(){
        log.debug("execute2");
        System.out.println("execute2");
    }
}
