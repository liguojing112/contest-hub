package com.example.jingsai.controller;

import com.example.jingsai.entity.Review;
import com.example.jingsai.service.ReviewService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyReviews() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        List<Review> reviews = reviewService.getByReviewerId(userInfo.getUserId());
        response.put("success", true);
        response.put("data", reviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<Map<String, Object>> getByCompetition(@PathVariable Long competitionId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> reviews = reviewService.getByCompetitionId(competitionId);
        response.put("success", true);
        response.put("data", reviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<Map<String, Object>> getByRegistration(@PathVariable Long registrationId) {
        Map<String, Object> response = new HashMap<>();
        Review review = reviewService.getByRegistrationId(registrationId);
        response.put("success", true);
        response.put("data", review);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitReview(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        Long competitionId = Long.parseLong(request.get("competitionId").toString());
        Long registrationId = Long.parseLong(request.get("registrationId").toString());
        Integer score = Integer.parseInt(request.get("score").toString());
        String comment = (String) request.getOrDefault("comment", "");

        boolean success = reviewService.submitReview(competitionId, registrationId, userInfo.getUserId(), score, comment);
        response.put("success", success);
        response.put("message", success ? "评审提交成功" : "评审提交失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignReviewer(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Long competitionId = Long.valueOf(request.get("competitionId").toString());
        Long registrationId = Long.valueOf(request.get("registrationId").toString());
        Long reviewerId = Long.valueOf(request.get("reviewerId").toString());
        boolean success = reviewService.assignReviewer(competitionId, registrationId, reviewerId);
        response.put("success", success);
        response.put("message", success ? "评审分配成功" : "分配失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auto-assign")
    public ResponseEntity<Map<String, Object>> autoAssignReviewers(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Long competitionId = Long.valueOf(request.get("competitionId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> idsRaw = (List<Integer>) request.get("reviewerIds");
        List<Long> reviewerIds = new ArrayList<>();
        for (Object id : idsRaw) {
            reviewerIds.add(Long.valueOf(id.toString()));
        }
        List<Review> assigned = reviewService.autoAssignReviewers(competitionId, reviewerIds);
        response.put("success", true);
        response.put("message", "已分配 " + assigned.size() + " 个评审任务");
        response.put("data", assigned);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/{competitionId}")
    public ResponseEntity<Map<String, Object>> getPending(@PathVariable Long competitionId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> pending = reviewService.getPendingByCompetitionId(competitionId);
        response.put("success", true);
        response.put("data", pending);
        return ResponseEntity.ok(response);
    }
}
