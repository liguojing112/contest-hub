package com.example.jingsai.controller;

import com.example.jingsai.entity.Competition;
import com.example.jingsai.service.CompetitionService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞赛控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    /**
     * 获取竞赛列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCompetitions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Map<String, Object> response = new HashMap<>();
        List<Competition> competitions;

        if (status != null && !status.isEmpty()) {
            competitions = competitionService.getByStatus(status);
        } else {
            competitions = competitionService.list();
        }

        response.put("success", true);
        response.put("data", Map.of(
                "content", competitions,
                "totalPages", (int) Math.ceil((double) competitions.size() / size),
                "totalElements", competitions.size()));
        return ResponseEntity.ok(response);
    }

    /**
     * 获取竞赛详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompetition(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Competition competition = competitionService.getById(id);

        if (competition != null) {
            response.put("success", true);
            response.put("data", competition);
        } else {
            response.put("success", false);
            response.put("message", "竞赛不存在");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 创建竞赛
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompetition(@RequestBody Competition competition) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        boolean success = competitionService.createCompetition(
                competition, userInfo.getUserId(), userInfo.getName());

        if (success) {
            response.put("success", true);
            response.put("message", "竞赛创建成功");
            response.put("data", competition);
            log.info("用户创建竞赛: {}, {}", userInfo.getName(), competition.getName());
        } else {
            response.put("success", false);
            response.put("message", "创建失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 更新竞赛
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompetition(
            @PathVariable Long id,
            @RequestBody Competition competition) {

        Map<String, Object> response = new HashMap<>();
        Competition existing = competitionService.getById(id);

        if (existing == null) {
            response.put("success", false);
            response.put("message", "竞赛不存在");
            return ResponseEntity.ok(response);
        }

        competition.setId(id);
        competition.setUpdateTime(existing.getUpdateTime());
        boolean success = competitionService.updateById(competition);

        if (success) {
            response.put("success", true);
            response.put("message", "竞赛更新成功");
            response.put("data", competition);
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 删除竞赛
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCompetition(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = competitionService.removeById(id);

        if (success) {
            response.put("success", true);
            response.put("message", "竞赛删除成功");
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取我的竞赛（教师）
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyCompetitions() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        List<Competition> competitions = competitionService.getByCreatorId(userInfo.getUserId());
        response.put("success", true);
        response.put("data", competitions);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取我的竞赛统计数据（教师）
     */
    @GetMapping("/my/stats")
    public ResponseEntity<Map<String, Object>> getMyCompetitionStats() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        List<Competition> competitions = competitionService.getByCreatorId(userInfo.getUserId());

        long total = competitions.size();
        long pending = competitions.stream().filter(c -> "pending".equals(c.getStatus())).count();
        long preparing = competitions.stream().filter(c -> "preparing".equals(c.getStatus())).count();
        long enrolling = competitions.stream().filter(c -> "enrolling".equals(c.getStatus())).count();
        long ready = competitions.stream().filter(c -> "ready".equals(c.getStatus())).count();
        long active = competitions.stream().filter(c -> "active".equals(c.getStatus())).count();
        long reviewing = competitions.stream().filter(c -> "reviewing".equals(c.getStatus())).count();
        long totalRegistrations = competitions.stream()
                .mapToLong(c -> c.getRegistered() != null ? c.getRegistered() : 0).sum();

        long activeCompetitions = pending + preparing + enrolling + ready + active + reviewing;

        response.put("success", true);
        response.put("data", Map.of(
                "total", total,
                "pending", pending,
                "preparing", preparing,
                "enrolling", enrolling,
                "ready", ready,
                "active", active,
                "reviewing", reviewing,
                "totalRegistrations", totalRegistrations,
                "activeCompetitions", activeCompetitions));
        return ResponseEntity.ok(response);
    }

    /**
     * 获取需要我评审的竞赛
     */
    @GetMapping("/review")
    public ResponseEntity<Map<String, Object>> getMyReviewCompetitions() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        List<Competition> competitions = competitionService.getByReviewer(userInfo.getUserName());
        response.put("success", true);
        response.put("data", competitions);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取活跃竞赛
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCompetitions() {
        Map<String, Object> response = new HashMap<>();
        List<Competition> competitions = competitionService.getActiveCompetitions();
        response.put("success", true);
        response.put("data", competitions);
        return ResponseEntity.ok(response);
    }

    /**
     * 热度排行榜
     */
    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking() {
        Map<String, Object> response = new HashMap<>();
        List<Competition> all = competitionService.list();
        all.sort((a, b) -> Double.compare(
                b.getHeatValue() != null ? b.getHeatValue() : 0,
                a.getHeatValue() != null ? a.getHeatValue() : 0));
        response.put("success", true);
        response.put("data", all);
        return ResponseEntity.ok(response);
    }
}
