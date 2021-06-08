package com.nowcoder.community;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NowcoderCommunityApplication.class)
public class ElasticSearchTests {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private DiscussPostRepository repository;
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    public void testInsert(){
        repository.save(discussPostService.findDiscussPostById(241));
        repository.save(discussPostService.findDiscussPostById(242));
        repository.save(discussPostService.findDiscussPostById(243));
    }
}
