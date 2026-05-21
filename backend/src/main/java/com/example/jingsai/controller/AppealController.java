package com.example.jingsai.controller;

import com.example.jingsai.entity.Appeal;
import com.example.jingsai.service.AppealService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealService appealService;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyAppeals() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        List<Appeal> appeals = appealService.getByStudentId(userInfo.getUserId());
        response.put("success", true);
        response.put("data", appeals);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPending() {
        Map<String, Object> response = new HashMap<>();
        List<Appeal> appeals = appealService.getByStatus("pending");
        response.put("success", true);
        response.put("data", appeals);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submit(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        Long registrationId = ((Number) request.get("registrationId")).longValue();
        String content = (String) request.get("content");

        boolean success = appealService.submitAppeal(userInfo.getUserId(), registrationId, content);
        response.put("success", success);
        response.put("message", success ? "申诉提交成功" : "申诉提交失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/handle")
    public ResponseEntity<Map<String, Object>> handle(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String reply = request.get("reply");
        boolean success = appealService.handleAppeal(id, reply);
        response.put("success", success);
        response.put("message", success ? "申诉处理成功" : "处理失败");
        return ResponseEntity.ok(response);
    }
}
