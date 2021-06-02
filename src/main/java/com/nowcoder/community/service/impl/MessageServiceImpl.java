package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.mapper.MessageMapper;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * select *
     * from message
     * where id in (
     * select max(id) from message
     * where status != 2
     * and from_id != 1
     * and (from_id = #{userId} or to_id = #{userId})
     * group by conversation_id
     * )
     * order by id desc
     *
     * @param userId
     * @return
     */
    @Override
    public List<Message> findConversations(int userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        String inString = String.format("select max(id) from message " +
                "where status != 2 and from_id != 1 and (from_id = %d or to_id = %d) group by conversation_id", userId, userId);
        queryWrapper.inSql("id", inString);
        queryWrapper.groupBy("conversation_id").orderByDesc("id");
        return messageMapper.selectList(queryWrapper);
    }

    /**
     * select *
     * from message
     * where status != 2
     * and from_id != 1
     * and conversation_id = #{conversationId}
     * order by id desc
     *
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> getLetters(String conversationId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2)
                .ne("from_id", 1)
                .eq("conversation_id", conversationId)
                .orderByDesc("id");
        return messageMapper.selectList(queryWrapper);
    }

    /**
     * select count(id)
     * from message
     * where status = 0
     * and from_id != 1
     * and to_id = #{userId}
     * <if test="conversationId != null">
     *      and conversation_id = #{conversationId}
     * </if>
     * @param userId
     * @param conversationId
     * @return
     */
    @Override
    public int getLetterUnreadCount(int userId, String conversationId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0)
                .ne("from_id", 1)
                .eq("to_id", userId);
        if (conversationId!= null){
            queryWrapper.eq("conversation_id", conversationId);
        }
        return messageMapper.selectCount(queryWrapper.select("id"));
    }

    /**
     * select count(id)
     * from message
     * where status != 2
     * and from_id != 1
     * and conversation_id = #{conversationId}
     * @param conversationId
     * @return
     */
    @Override
    public int getLetterCount(String conversationId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", 2)
                .ne("from_id", 1)
                .eq("conversation_id", conversationId);
        return messageMapper.selectCount(queryWrapper.select("id"));
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    /**
     * update message
     * set status = #{status}
     * where id in
     * <foreach collection="ids" item="id" open="(" separator="," close=")">
     *      #{id}
     * </foreach>
     * @param ids
     * @return
     */
    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateMessage(ids, 1);
    }

    @Override
    public int deleteMessage(int id) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        Message message = messageMapper.selectById(id);
        updateWrapper.eq("id", id);
        updateWrapper.set("status", 2);
        return messageMapper.update(message, updateWrapper);
    }
}
