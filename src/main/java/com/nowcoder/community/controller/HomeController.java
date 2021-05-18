package com.nowcoder.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model,
                               @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        List<DiscussPost> list = discussPostService.findDiscussPosts(0);
        PageHelper.startPage(pageNum, 10);
        PageInfo<DiscussPost> posts = new PageInfo<>(list);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list.size() > 0){
            for (DiscussPost post: list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        PageHelper.startPage(pageNum, 10);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(discussPosts);
        log.info("Pages: " + pageInfo.getPages() + " PageNum: " + pageInfo.getPageNum() +" PageSize: " + pageInfo.getPageSize());
        model.addAttribute("discussPosts", pageInfo);
        return "/index";
    }

}
