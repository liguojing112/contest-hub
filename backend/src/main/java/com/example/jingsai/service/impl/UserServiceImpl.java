package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.User;
import com.example.jingsai.mapper.UserMapper;
import com.example.jingsai.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getByUsername(String username) {
        return getBaseMapper().selectByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return getBaseMapper().countByUsername(username) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return getBaseMapper().countByPhone(phone) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return getBaseMapper().countByEmail(email) > 0;
    }

    @Override
    public List<User> getByRole(String role) {
        return getBaseMapper().selectByRole(role);
    }

    @Override
    public User login(String username, String password) {
        User user = getByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return save(user);
    }

    @Override
    public boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}