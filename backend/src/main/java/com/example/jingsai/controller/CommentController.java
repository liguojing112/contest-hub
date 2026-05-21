package com.example.jingsai.controller;

import com.example.jingsai.entity.Comment;
import com.example.jingsai.service.CommentService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<Map<String, Object>> list(@PathVariable Long competitionId) {
        Map<String, Object> response = new HashMap<>();
        List<Comment> comments = commentService.getByCompetitionId(competitionId);
        response.put("success", true);
        response.put("data", comments);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        Long competitionId = request.get("competitionId") != null
                ? Long.valueOf(request.get("competitionId").toString()) : null;
        String content = (String) request.get("content");

        if (competitionId == null || content == null || content.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        boolean success = commentService.addComment(competitionId, userInfo.getUserId(),
                userInfo.getName(), content.trim());
        response.put("success", success);
        response.put("message", success ? "评论成功" : "评论失败");
        return ResponseEntity.ok(response);
    }
}
