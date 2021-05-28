package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import com.nowcoder.community.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type", entityType)
                .eq("entity_id", entityId)
                .eq("status", 0)
                .orderByAsc("create_time");
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type", entityType)
                .eq("entity_id", entityId)
                .eq("status", 0);
        return commentMapper.selectCount(queryWrapper.select("id"));
    }
}
