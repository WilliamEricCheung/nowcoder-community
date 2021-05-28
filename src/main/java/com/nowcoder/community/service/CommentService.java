package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntity(int entityType, int entityId);
    int findCommentCount(int entityType, int entityId);
}
