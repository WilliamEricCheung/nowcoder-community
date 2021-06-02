package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;

import java.util.List;

public interface MessageService {

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> findConversations(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> getLetters(String conversationId);

    // 查询未读私信的数量
    int getLetterUnreadCount(int userId, String conversationId);

    // 查询某个会话包含的私信数量
    int getLetterCount(String conversationId);

    // 新增消息
    int addMessage(Message message);

    // 修改消息的状态
    int readMessage(List<Integer> ids);

}
