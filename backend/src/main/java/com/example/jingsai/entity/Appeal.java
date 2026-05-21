package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appeal")
public class Appeal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long registrationId;
    private Long studentId;
    private String content;
    private String reply;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime replyTime;
}
