package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Message;
import com.example.jingsai.mapper.MessageMapper;
import com.example.jingsai.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public List<Message> getByUserId(Long userId) {
        return getBaseMapper().selectByUserId(userId);
    }

    @Override
    public List<Message> getByUserIdAndStatus(Long userId, String status) {
        return getBaseMapper().selectByUserIdAndStatus(userId, status);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return getBaseMapper().countUnreadByUserId(userId);
    }

    @Override
    public boolean sendSystemMessage(Long userId, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setSenderId(null);
        message.setSenderName("系统");
        message.setType("system");
        message.setContent(content);
        message.setStatus("unread");
        message.setSendTime(LocalDateTime.now());
        return save(message);
    }

    @Override
    public boolean sendUserMessage(Long userId, Long senderId, String senderName, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setType("competition");
        message.setContent(content);
        message.setStatus("unread");
        message.setSendTime(LocalDateTime.now());
        return save(message);
    }

    @Override
    public boolean markAsRead(Long id) {
        Message message = getById(id);
        if (message != null) {
            message.setStatus("read");
            return updateById(message);
        }
        return false;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        List<Message> messages = getByUserIdAndStatus(userId, "unread");
        messages.forEach(m -> m.setStatus("read"));
        return updateBatchById(messages);
    }

    @Override
    public boolean sendPersonalMessage(Long fromUserId, String fromUserName, Long toUserId, String content) {
        Message message = new Message();
        message.setUserId(toUserId);
        message.setSenderId(fromUserId);
        message.setSenderName(fromUserName);
        message.setType("personal");
        message.setContent(content);
        message.setStatus("unread");
        message.setSendTime(LocalDateTime.now());
        return save(message);
    }
}