package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报名实体类
 */
@Data
@TableName("registration")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;            // 用户ID
    private Long competitionId;     // 竞赛ID
    private Long teamId;            // 团队ID(可选)
    private String status;          // 状态(pending/approved/rejected)
    private LocalDateTime registerTime;
}