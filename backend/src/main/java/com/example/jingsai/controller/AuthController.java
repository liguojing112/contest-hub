package com.example.jingsai.controller;

import com.example.jingsai.entity.User;
import com.example.jingsai.service.UserService;
import com.example.jingsai.util.JwtUtil;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.login(username, password);
        if (user != null) {
            String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole());
            userInfo.put("college", user.getCollege());
            userInfo.put("major", user.getMajor());
            userInfo.put("className", user.getClassName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatar", user.getAvatar());
            response.put("user", userInfo);
            log.info("用户登录成功: {}", username);
            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "用户名或密码错误");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userService.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.ok(response);
        }

        if (userService.existsByPhone(user.getPhone())) {
            response.put("success", false);
            response.put("message", "该手机号已被注册，每人只能注册一个账号");
            return ResponseEntity.ok(response);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty() && userService.existsByEmail(user.getEmail())) {
            response.put("success", false);
            response.put("message", "该邮箱已被注册，每人只能注册一个账号");
            return ResponseEntity.ok(response);
        }

        boolean success = userService.register(user);
        if (success) {
            response.put("success", true);
            response.put("message", "注册成功");
            log.info("用户注册成功: {}", user.getUsername());
        } else {
            response.put("success", false);
            response.put("message", "注册失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        boolean success = userService.updateUser(user);
        if (success) {
            response.put("success", true);
            response.put("message", "信息更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 忘记密码
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String phone = request.get("phone");
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        User user = userService.getByUsername(username);
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }

        boolean verified = false;
        if (phone != null && !phone.isEmpty() && phone.equals(user.getPhone())) {
            verified = true;
        }
        if (email != null && !email.isEmpty() && email.equals(user.getEmail())) {
            verified = true;
        }

        if (!verified) {
            response.put("success", false);
            response.put("message", "手机号或邮箱验证失败");
            return ResponseEntity.ok(response);
        }

        user.setPassword(userService.encodePassword(newPassword));
        boolean success = userService.updateUser(user);
        response.put("success", success);
        response.put("message", success ? "密码重置成功" : "密码重置失败");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取教师列表
     */
    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> getTeachers() {
        Map<String, Object> response = new HashMap<>();
        List<User> teachers = userService.getByRole("TEACHER");
        response.put("success", true);
        response.put("data", teachers);
        return ResponseEntity.ok(response);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null || newPassword.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        User user = userService.getById(userInfo.getUserId());
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }

        if (!userService.verifyPassword(oldPassword, user.getPassword())) {
            response.put("success", false);
            response.put("message", "原密码错误");
            return ResponseEntity.ok(response);
        }

        user.setPassword(userService.encodePassword(newPassword.trim()));
        boolean success = userService.updateUser(user);
        response.put("success", success);
        response.put("message", success ? "密码修改成功" : "修改失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择文件");
            return ResponseEntity.ok(response);
        }

        try {
            Path uploadDir = Paths.get("./uploads/avatars").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String ext = ".png";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = userInfo.getUserId() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            String avatarUrl = "/uploads/avatars/" + filename;
            User user = userService.getById(userInfo.getUserId());
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateUser(user);
            }

            response.put("success", true);
            response.put("data", Map.of("avatar", avatarUrl));
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
