package com.nowcoder.community.controller;

import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class SearchController implements Constant {

    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(defaultValue = "", value = "keyword") String keyword,
                         @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        // 搜索帖子
        Page<DiscussPost> searchResult = elasticSearchService.searchDiscussPost(keyword, pageNum - 1, 10);
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null){
            for (DiscussPost post: searchResult){
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞的数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(discussPosts, pageNum, 10, (int) searchResult.getTotalElements());
        model.addAttribute("discussPosts", pageInfo);
        model.addAttribute("keyword", keyword);

        return "/site/search";
    }
}
