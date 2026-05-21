package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("team_member")
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;
    private Long userId;
    private String role;
    private String status;
    private LocalDateTime joinTime;
}
