package com.nowcoder.community.service.impl;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.mapper.MessageMapper;
import com.nowcoder.community.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> findConversations(int userId) {
        return null;
    }

    @Override
    public List<Message> getLetters(String conversationId) {
        return null;
    }

    @Override
    public int getLetterUnreadCount(int userId, String conversationId) {
        return 0;
    }
}
