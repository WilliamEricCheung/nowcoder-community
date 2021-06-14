package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import reactor.util.annotation.Nullable;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter filter;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口：Cache，LoadingCache，AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    @PostConstruct
    public void init(){
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception{
                        if (key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        // 二级缓存：Redis -> mysql
                        log.debug("load post list from DB.");
                        return findDiscussPosts(0, 1);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int orderMode) {
        QueryWrapper<DiscussPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2);
        if (userId != 0)
            queryWrapper.eq("user_id", userId);
        if (orderMode == 0){
            queryWrapper.orderByDesc("type","create_time");
        }
        if (orderMode == 1){
            queryWrapper.orderByDesc("type","score", "create_time");
        }
//        if (userId == 0 && orderMode == 1){
//            return postListCache.get("postCache");
//        }
        log.debug("load post list from DB.");
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

    @Override
    public int updateType(int id, int type) {
        DiscussPost post = findDiscussPostById(id);
        UpdateWrapper<DiscussPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("type", type);
        return discussPostMapper.update(post, updateWrapper);
    }

    @Override
    public int updateStatus(int id, int status) {
        DiscussPost post = findDiscussPostById(id);
        UpdateWrapper<DiscussPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("status", status);
        return discussPostMapper.update(post, updateWrapper);
    }

    @Override
    public int updateScore(int id, double score) {
        DiscussPost post = findDiscussPostById(id);
        UpdateWrapper<DiscussPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("score", score);
        return discussPostMapper.update(post, updateWrapper);
    }
}
