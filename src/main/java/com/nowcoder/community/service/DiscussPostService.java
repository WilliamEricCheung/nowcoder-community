package com.nowcoder.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nowcoder.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId, int orderMode);
    IPage<DiscussPost> findDiscussPosts(int userId, int orderMode, int pageNum, int pageSize);
    DiscussPost findDiscussPostById(int id);
    int addDiscussPost(DiscussPost post);
    int updateCommentCount(int id, int commentCount);
    int updateType(int id, int type);
    int updateStatus(int id, int status);
    int updateScore(int id, double score);

}
