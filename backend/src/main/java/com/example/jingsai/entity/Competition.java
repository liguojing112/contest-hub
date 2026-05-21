package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 竞赛实体类
 */
@Data
@TableName("competition")
public class Competition {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 竞赛名称
    private String type;
    private Long categoryId;
    private String description;
    private String creator;
    private Long creatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registerDeadline;
    private Integer maxTeamSize;
    private Integer registered;
    private String status;
    private String reviewers;
    private Double heatValue;
    private Long viewCount;
    private Long messageCount;
    private Boolean uploadWorks;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}