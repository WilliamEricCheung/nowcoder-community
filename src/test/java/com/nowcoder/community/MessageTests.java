package com.nowcoder.community;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NowcoderCommunityApplication.class)
public class MessageTests {

    @Autowired
    private MessageService messageService;

    @Test
    public void testFindConversations(){
        List<Message> messageList = messageService.findConversations(111);
        for (Message message: messageList){
            System.out.println(message);
        }
        System.out.println("----");
        messageList = messageService.getLetters("111_112");
        for (Message message: messageList){
            System.out.println(message);
        }
        int unread = messageService.getLetterUnreadCount(131,"111_131");
        System.out.println(unread);
    }

    @Test
    public void testDeleteMsg(){
        System.out.println(messageService.deleteMessage(1));
    }
}
