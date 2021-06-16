package com.nowcoder.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController implements Constant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String index() {
         return "forward:/index";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model,
                               @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
                               @RequestParam(defaultValue = "0", value = "orderMode") int orderMode) {
//        List<DiscussPost> list = discussPostService.findDiscussPosts(0, orderMode);
        IPage<DiscussPost> postIPage = discussPostService.findDiscussPosts(0, orderMode, pageNum, 10);
        List<DiscussPost> list = postIPage.getRecords();
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(discussPosts,pageNum,10, (int) postIPage.getTotal());
//        log.info("Pages: " + pageInfo.getPages() + " PageNum: " + pageInfo.getPageNum() + " PageSize: " + pageInfo.getPageSize());
        model.addAttribute("discussPosts", pageInfo);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

}
