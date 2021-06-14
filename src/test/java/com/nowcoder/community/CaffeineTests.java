package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NowcoderCommunityApplication.class)
public class CaffeineTests {

    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest(){
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("不容乐观");
            post.setCreateTime(new Date());
            post.setScore(Math.random()*2000);
            postService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0, 1));
        System.out.println(postService.findDiscussPosts(0, 1));
        System.out.println(postService.findDiscussPosts(0, 1));
        System.out.println(postService.findDiscussPosts(0, 0));
    }
}
