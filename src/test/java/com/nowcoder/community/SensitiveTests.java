package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NowcoderCommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter filter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，可以嫖☆娼，可以吸毒，可以开票，哈哈哈！";
        text = filter.filter(text);
        System.out.println(text);
    }
}
