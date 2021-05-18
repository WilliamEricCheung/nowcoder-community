package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTests {

    @Autowired
    private UserService userService;

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
}
