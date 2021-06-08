package com.nowcoder.community.controller;

import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.Constant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.PageUtil;
import com.nowcoder.community.util.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FollowController implements Constant {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return ProjectUtil.getJSONString(0, "已关注！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return ProjectUtil.getJSONString(0, "已取消关注！");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Model model,
                               @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        List<Map<String, Object>> userList = followService.findFollowees(userId);
        if (userList != null){
            for (Map<String, Object> map: userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(userList, pageNum, 5);
        model.addAttribute("users", pageInfo);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Model model,
                               @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        List<Map<String, Object>> userList = followService.findFollowers(userId);
        if (userList != null){
            for (Map<String, Object> map: userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(userList, pageNum, 5);
        model.addAttribute("users", pageInfo);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId){
        if (hostHolder.getUser() == null) return false;
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}
