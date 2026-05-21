package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;            // 接收用户ID
    private Long senderId;          // 发送者ID(系统消息为null)
    private String senderName;      // 发送者名称
    private String type;            // 消息类型(system/competition/team)
    private String content;         // 消息内容
    private String status;          // 状态(unread/read)
    private LocalDateTime sendTime;
}