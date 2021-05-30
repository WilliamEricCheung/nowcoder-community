package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter filter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId) {
        QueryWrapper<DiscussPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2);
        if (userId != 0)
            queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("type","create_time");
        return discussPostMapper.selectList(queryWrapper);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectById(id);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent((HtmlUtils.htmlEscape(post.getContent())));
        // 过滤敏感词
        post.setTitle(filter.filter(post.getTitle()));
        post.setContent(filter.filter(post.getContent()));
        return discussPostMapper.insert(post);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        DiscussPost post = findDiscussPostById(id);
        UpdateWrapper<DiscussPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("comment_count", commentCount);
        return discussPostMapper.update(post, updateWrapper);
    }
}
