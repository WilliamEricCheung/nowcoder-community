package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.service.DiscussPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper mapper;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId) {
        QueryWrapper<DiscussPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2);
        if (userId != 0)
            queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("type","create_time");
        return mapper.selectList(queryWrapper);
    }
}
