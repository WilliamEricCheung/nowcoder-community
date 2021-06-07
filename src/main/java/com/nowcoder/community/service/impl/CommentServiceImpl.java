package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService, Constant {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

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

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows =  commentMapper.insert(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = findCommentCount(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    @Override
    public List<Comment> findCommentsByEntityAndUserId(int entityType, int userId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type", entityType)
                .eq("user_id", userId)
                .eq("status", 0)
                .orderByDesc("create_time");
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public int findCommentCountByUser(int entityType, int userId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type", entityType)
                .eq("user_id", userId)
                .eq("status", 0);
        return commentMapper.selectCount(queryWrapper.select("id"));
    }
}
