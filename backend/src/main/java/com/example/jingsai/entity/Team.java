package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("team")
public class Team {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Long competitionId;
    private Long leaderId;
    private String teamCode;
    private String declaration;
    private String status;
    private Long advisorId;
    private String auditStatus;
    private String auditComment;
    private LocalDateTime createTime;
}
