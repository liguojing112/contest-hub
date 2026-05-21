package com.example.jingsai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;        // 用户名/学号/工号
    private String password;        // 密码(加密后)
    private String name;            // 姓名
    private String role;            // 角色(ADMIN/TEACHER/STUDENT)
    private String college;         // 学院
    private String major;           // 专业
    private String className;       // 班级
    private String phone;           // 联系电话
    private String email;           // 邮箱
    private String workTime;
    private String avatar;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}