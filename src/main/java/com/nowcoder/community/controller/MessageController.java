package com.nowcoder.community.controller;

import com.github.pagehelper.PageInfo;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.PageUtil;
import com.nowcoder.community.util.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model,
                                @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        User user = hostHolder.getUser();
        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList!= null){
            for (Message message: conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.getLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.getLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId()? message.getToId(): message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(conversations, pageNum, 5);
        model.addAttribute("conversations", pageInfo);

        // 查询未读消息数量
        int letterUnreadCount = messageService.getLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,
                                  @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum){
        // 私信列表
        List<Message> letterList = messageService.getLetters(conversationId);
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null){
            for (Message message: letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        PageInfo<Map<String, Object>> pageInfo = PageUtil.startPage(letters, pageNum, 5);
        model.addAttribute("letters", pageInfo);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message: letterList){
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){
        User target = userService.findUserByName(toName);
        if (target == null){
            return ProjectUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+ message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+ message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return ProjectUtil.getJSONString(0);
    }

    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteLetter(String id){
        int result = messageService.deleteMessage(Integer.parseInt(id));
        if (result == 1) {
            return ProjectUtil.getJSONString(0);
        }else{
            return ProjectUtil.getJSONString(1, "删除失败！");
        }
    }
}
