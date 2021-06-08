package com.nowcoder.community;

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
        List<DiscussPost> posts = postService.findDiscussPosts(0);
        for (DiscussPost post: posts)
            System.out.println(post);
    }
}
