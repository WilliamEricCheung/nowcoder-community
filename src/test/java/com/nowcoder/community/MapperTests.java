package com.nowcoder.community;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTests {

    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService postService;
    @Autowired
    private DiscussPostMapper postMapper;

    @Test
    public void testFindUserId(){
        User user = userService.findUserById(101);
        System.out.println(user);
    }

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("testAdd");
        user.setPassword("pwd");
        int result = userService.addUser(user);
        System.out.println(result);
    }

    @Test
    public void testUpdateStatus(){
        int result = userService.updateStatus(150, 1);
        System.out.println(result);
    }

    @Test
    public void testFindPosts(){
        List<DiscussPost> posts = postService.findDiscussPosts(0, 0);
        for (DiscussPost post: posts)
            System.out.println(post);
    }

    @Test
    public void testSelectPage(){
        QueryWrapper<DiscussPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2);
        Page<DiscussPost> postPage = new Page<>(1, 5);
        IPage<DiscussPost> postIPage = postMapper.selectPage(postPage, queryWrapper);
        System.out.println("总页数："+postIPage.getPages());
        System.out.println("总纪录数："+postIPage.getTotal());
        postIPage.getRecords().forEach(System.out::println);
    }
}
