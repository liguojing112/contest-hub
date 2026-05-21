package com.example.jingsai.controller;

import com.example.jingsai.entity.Registration;
import com.example.jingsai.entity.User;
import com.example.jingsai.service.RegistrationService;
import com.example.jingsai.service.UserService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报名控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    /**
     * 获取我的报名记录
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyRegistrations() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        List<Registration> registrations = registrationService.getByUserId(userInfo.getUserId());
        response.put("success", true);
        response.put("data", registrations);
        return ResponseEntity.ok(response);
    }

    /**
     * 报名竞赛
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        Long competitionId = Long.parseLong(request.get("competitionId").toString());
        Long teamId = request.get("teamId") != null ? Long.parseLong(request.get("teamId").toString()) : null;

        // 检查是否已报名
        if (registrationService.isRegistered(userInfo.getUserId(), competitionId)) {
            response.put("success", false);
            response.put("message", "您已报名该竞赛");
            return ResponseEntity.ok(response);
        }

        boolean success = registrationService.createRegistration(userInfo.getUserId(), competitionId, teamId);
        if (success) {
            response.put("success", true);
            response.put("message", "报名成功");
            log.info("用户报名竞赛: {}, competitionId: {}", userInfo.getUserName(), competitionId);
        } else {
            response.put("success", false);
            response.put("message", "报名失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 取消报名
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelRegistration(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = registrationService.cancelRegistration(id);

        if (success) {
            response.put("success", true);
            response.put("message", "取消报名成功");
        } else {
            response.put("success", false);
            response.put("message", "取消失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取竞赛报名列表（教师/管理员）
     */
    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<Map<String, Object>> getCompetitionRegistrations(@PathVariable Long competitionId) {
        Map<String, Object> response = new HashMap<>();
        List<Registration> registrations = registrationService.getByCompetitionId(competitionId);
        response.put("success", true);
        response.put("data", registrations);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新报名状态
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();
        String status = request.get("status");

        boolean success = registrationService.updateStatus(id, status);
        if (success) {
            response.put("success", true);
            response.put("message", "状态更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/{competitionId}")
    public ResponseEntity<byte[]> exportRegistrations(@PathVariable Long competitionId) {
        List<Registration> registrations = registrationService.getByCompetitionId(competitionId);
        StringBuilder csv = new StringBuilder();
        csv.append("﻿"); // BOM for Excel UTF-8
        csv.append("学号,姓名,学院,专业,报名时间,状态\n");
        for (Registration r : registrations) {
            User user = userService.getById(r.getUserId());
            if (user != null) {
                csv.append(user.getUsername()).append(",")
                   .append(user.getName() != null ? user.getName() : "").append(",")
                   .append(user.getCollege() != null ? user.getCollege() : "").append(",")
                   .append(user.getMajor() != null ? user.getMajor() : "").append(",")
                   .append(r.getRegisterTime() != null ? r.getRegisterTime() : "").append(",")
                   .append(r.getStatus() != null ? r.getStatus() : "").append("\n");
            }
        }
        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv;charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=registrations_" + competitionId + ".csv")
                .body(bytes);
    }
}