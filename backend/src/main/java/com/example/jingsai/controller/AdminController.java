package com.example.jingsai.controller;

import com.example.jingsai.entity.Appeal;
import com.example.jingsai.entity.Competition;
import com.example.jingsai.entity.Message;
import com.example.jingsai.entity.Registration;
import com.example.jingsai.entity.User;
import com.example.jingsai.service.AppealService;
import com.example.jingsai.service.CompetitionService;
import com.example.jingsai.service.MessageService;
import com.example.jingsai.service.RegistrationService;
import com.example.jingsai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CompetitionService competitionService;
    private final MessageService messageService;
    private final AppealService appealService;
    private final RegistrationService registrationService;

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(required = false) String role) {
        Map<String, Object> response = new HashMap<>();
        List<User> users;

        if (role != null && !role.isEmpty()) {
            users = userService.getByRole(role.toUpperCase());
        } else {
            users = userService.list();
        }

        response.put("success", true);
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getById(id);

        if (user != null) {
            response.put("success", true);
            response.put("data", user);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 管理员创建用户
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名不能为空");
            return ResponseEntity.ok(response);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "密码不能为空");
            return ResponseEntity.ok(response);
        }
        if (userService.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.ok(response);
        }

        boolean success = userService.register(user);
        response.put("success", success);
        response.put("message", success ? "用户创建成功" : "创建失败");
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息（管理员）
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        Map<String, Object> response = new HashMap<>();
        User existing = userService.getById(id);

        if (existing == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }

        user.setId(id);
        boolean success = userService.updateUser(user);

        if (success) {
            response.put("success", true);
            response.put("message", "用户信息更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.removeById(id);

        if (success) {
            response.put("success", true);
            response.put("message", "用户删除成功");
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();

        // 获取用户统计
        List<User> allUsers = userService.list();
        long studentCount = allUsers.stream().filter(u -> "STUDENT".equals(u.getRole())).count();
        long teacherCount = allUsers.stream().filter(u -> "TEACHER".equals(u.getRole())).count();
        long adminCount = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();

        // 获取竞赛统计
        List<?> competitions = competitionService.list();
        long competitionCount = competitions.size();

        response.put("success", true);
        response.put("data", Map.of(
                "totalUsers", allUsers.size(),
                "studentCount", studentCount,
                "teacherCount", teacherCount,
                "adminCount", adminCount,
                "competitionCount", competitionCount
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/users/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getById(id);
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response);
        }
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        userService.updateUser(user);
        response.put("success", true);
        response.put("message", "状态更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理员统计数据
     */
    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> response = new HashMap<>();
        List<User> allUsers = userService.list();
        List<?> competitions = competitionService.list();

        long studentCount = allUsers.stream().filter(u -> "STUDENT".equals(u.getRole())).count();
        long teacherCount = allUsers.stream().filter(u -> "TEACHER".equals(u.getRole())).count();
        long adminCount = allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long totalRegistrations = competitions.stream()
                .mapToLong(c -> ((com.example.jingsai.entity.Competition) c).getRegistered() != null
                        ? ((com.example.jingsai.entity.Competition) c).getRegistered() : 0)
                .sum();

        long pendingCompetitionCount = competitions.stream()
                .filter(c -> "pending".equals(((com.example.jingsai.entity.Competition) c).getStatus()))
                .count();
        List<Message> allMessages = messageService.list();
        long unreadMessageCount = allMessages.stream()
                .filter(m -> "unread".equals(m.getStatus()))
                .count();
        List<Appeal> allAppeals = appealService.list();
        long pendingAppealCount = allAppeals.stream()
                .filter(a -> "pending".equals(a.getStatus()))
                .count();

        response.put("success", true);
        response.put("data", Map.of(
                "totalUsers", allUsers.size(),
                "studentCount", studentCount,
                "teacherCount", teacherCount,
                "adminCount", adminCount,
                "competitionCount", competitions.size(),
                "totalRegistrations", totalRegistrations,
                "pendingCompetitionCount", pendingCompetitionCount,
                "unreadMessageCount", unreadMessageCount,
                "pendingAppealCount", pendingAppealCount
        ));
        return ResponseEntity.ok(response);
    }

    /**
     * 按学院统计
     */
    @GetMapping("/stats/by-college")
    public ResponseEntity<Map<String, Object>> getStatsByCollege() {
        Map<String, Object> response = new HashMap<>();
        List<User> students = userService.getByRole("STUDENT");
        Map<String, Long> collegeCount = students.stream()
                .filter(s -> s.getCollege() != null)
                .collect(java.util.stream.Collectors.groupingBy(User::getCollege, java.util.stream.Collectors.counting()));
        response.put("success", true);
        response.put("data", collegeCount);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有消息（管理员视图）
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getAllMessages() {
        Map<String, Object> response = new HashMap<>();
        List<com.example.jingsai.entity.Message> messages = messageService.list();
        messages.sort((a, b) -> b.getSendTime().compareTo(a.getSendTime()));
        response.put("success", true);
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新竞赛状态（管理员审核）
     */
    @PostMapping("/competitions/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCompetitionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();
        String status = request.get("status");

        boolean success = competitionService.updateStatus(id, status);
        if (success) {
            // 通知竞赛创建者
            Competition competition = competitionService.getById(id);
            if (competition != null && competition.getCreatorId() != null) {
                String statusText = "preparing".equals(status) ? "通过" : "rejected".equals(status) ? "拒绝" : status;
                messageService.sendSystemMessage(competition.getCreatorId(),
                        "您的竞赛「" + competition.getName() + "」已被管理员" + statusText);
            }
            response.put("success", true);
            response.put("message", "竞赛状态更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 月度报名趋势统计
     */
    @GetMapping("/stats/monthly-registrations")
    public ResponseEntity<Map<String, Object>> getMonthlyRegistrations() {
        Map<String, Object> response = new HashMap<>();
        List<Registration> registrations = registrationService.list();

        // 按月份统计报名数量
        Map<Integer, Long> monthlyCount = new java.util.LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyCount.put(i, 0L);
        }
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentYear = now.getYear();

        for (Registration r : registrations) {
            if (r.getRegisterTime() != null && r.getRegisterTime().getYear() == currentYear) {
                int month = r.getRegisterTime().getMonthValue();
                monthlyCount.merge(month, 1L, Long::sum);
            }
        }

        response.put("success", true);
        response.put("data", Map.of(
                "months", monthlyCount.keySet().stream().map(m -> m + "月").toList(),
                "counts", monthlyCount.values().stream().toList(),
                "year", currentYear
        ));
        return ResponseEntity.ok(response);
    }

    /**
     * 各竞赛报名人数统计
     */
    @GetMapping("/stats/competition-registrations")
    public ResponseEntity<Map<String, Object>> getCompetitionRegistrations() {
        Map<String, Object> response = new HashMap<>();
        List<Competition> competitions = competitionService.list();
        List<Registration> registrations = registrationService.list();

        // 统计每个竞赛的报名人数
        Map<Long, Long> compRegCount = registrations.stream()
                .collect(java.util.stream.Collectors.groupingBy(Registration::getCompetitionId, java.util.stream.Collectors.counting()));

        // 取报名人数前10的竞赛
        List<Map<String, Object>> topCompetitions = competitions.stream()
                .sorted((a, b) -> {
                    long countB = compRegCount.getOrDefault(b.getId(), 0L);
                    long countA = compRegCount.getOrDefault(a.getId(), 0L);
                    return Long.compare(countB, countA);
                })
                .limit(10)
                .map(c -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", c.getName());
                    item.put("count", compRegCount.getOrDefault(c.getId(), 0L));
                    item.put("type", c.getType());
                    return item;
                })
                .toList();

        response.put("success", true);
        response.put("data", topCompetitions);
        return ResponseEntity.ok(response);
    }
}