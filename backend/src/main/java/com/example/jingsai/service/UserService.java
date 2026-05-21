package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.User;

import java.util.List;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    List<User> getByRole(String role);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 验证密码
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 加密密码
     */
    String encodePassword(String rawPassword);
}