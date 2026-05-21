package com.example.jingsai.controller;

import com.example.jingsai.entity.Message;
import com.example.jingsai.entity.User;
import com.example.jingsai.service.MessageService;
import com.example.jingsai.service.UserService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    /**
     * 获取我的消息列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMessages(@RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        List<Message> messages;
        if (status != null && !status.isEmpty()) {
            messages = messageService.getByUserIdAndStatus(userInfo.getUserId(), status);
        } else {
            messages = messageService.getByUserId(userInfo.getUserId());
        }

        response.put("success", true);
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        int count = messageService.getUnreadCount(userInfo.getUserId());
        response.put("success", true);
        response.put("data", count);
        return ResponseEntity.ok(response);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = messageService.markAsRead(id);

        if (success) {
            response.put("success", true);
            response.put("message", "消息已标记为已读");
        } else {
            response.put("success", false);
            response.put("message", "操作失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 批量标记消息为已读
     */
    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        boolean success = messageService.markAllAsRead(userInfo.getUserId());
        if (success) {
            response.put("success", true);
            response.put("message", "所有消息已标记为已读");
        } else {
            response.put("success", false);
            response.put("message", "操作失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = messageService.removeById(id);

        if (success) {
            response.put("success", true);
            response.put("message", "消息删除成功");
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 批量发送消息（教师群发给报名学生）
     */
    @PostMapping("/batch-send")
    public ResponseEntity<Map<String, Object>> batchSend(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        @SuppressWarnings("unchecked")
        List<Object> userIdsRaw = (List<Object>) request.get("userIds");
        String content = (String) request.get("content");
        if (userIdsRaw == null || content == null) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        List<Long> userIds = userIdsRaw.stream()
                .map(n -> Long.valueOf(n.toString()))
                .toList();
        int successCount = 0;
        for (Long uid : userIds) {
            if (messageService.sendUserMessage(uid, userInfo.getUserId(), userInfo.getName(), content)) {
                successCount++;
            }
        }

        response.put("success", true);
        response.put("message", "已发送 " + successCount + " 条通知");
        return ResponseEntity.ok(response);
    }

    /**
     * 发送个人消息
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        Long toUserId = request.get("toUserId") != null
                ? Long.valueOf(request.get("toUserId").toString()) : null;
        String content = (String) request.get("content");
        if (toUserId == null || content == null || content.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }
        boolean success = messageService.sendPersonalMessage(
                userInfo.getUserId(), userInfo.getName(), toUserId, content.trim());
        response.put("success", success);
        response.put("message", success ? "消息发送成功" : "发送失败");
        return ResponseEntity.ok(response);
    }

    /**
     * 发送审核通知给管理员（由教师调用）
     */
    @PostMapping("/send-to-admin")
    public ResponseEntity<Map<String, Object>> sendToAdmin(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        String competitionName = (String) request.get("competitionName");
        Long competitionId = request.get("competitionId") != null ?
            Long.parseLong(request.get("competitionId").toString()) : null;

        User admin = userService.getByUsername("admin");
        if (admin == null) {
            response.put("success", false);
            response.put("message", "未找到管理员账号");
            return ResponseEntity.ok(response);
        }

        String content = String.format("教师 %s 创建了新竞赛 \"%s\"，需要管理员审核",
                userInfo.getName(), competitionName);

        boolean success = messageService.sendUserMessage(
                admin.getId(),
                userInfo.getUserId(),
                userInfo.getName(),
                content
        );

        if (success) {
            response.put("success", true);
            response.put("message", "审核通知已发送给管理员");
        } else {
            response.put("success", false);
            response.put("message", "发送失败");
        }

        return ResponseEntity.ok(response);
    }
}
