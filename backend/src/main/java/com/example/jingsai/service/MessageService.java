package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Message;

import java.util.List;

/**
 * 消息Service接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 根据用户ID查询消息列表
     */
    List<Message> getByUserId(Long userId);

    /**
     * 根据用户ID和状态查询消息列表
     */
    List<Message> getByUserIdAndStatus(Long userId, String status);

    /**
     * 获取未读消息数量
     */
    int getUnreadCount(Long userId);

    /**
     * 发送系统消息
     */
    boolean sendSystemMessage(Long userId, String content);

    /**
     * 发送用户消息
     */
    boolean sendUserMessage(Long userId, Long senderId, String senderName, String content);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long id);

    /**
     * 批量标记消息为已读
     */
    boolean markAllAsRead(Long userId);

    boolean sendPersonalMessage(Long fromUserId, String fromUserName, Long toUserId, String content);
}