package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer implements Constant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            log.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            log.error("消息格式错误！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry: event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            log.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            log.error("消息格式错误！");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            log.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            log.error("消息格式错误！");
            return;
        }

        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

}
