package com.example.jingsai.controller;

import com.example.jingsai.entity.Announcement;
import com.example.jingsai.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> response = new HashMap<>();
        List<Announcement> list = announcementService.list();
        response.put("success", true);
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Announcement announcement) {
        Map<String, Object> response = new HashMap<>();
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        if (announcement.getStatus() == null) {
            announcement.setStatus("published");
        }
        boolean success = announcementService.save(announcement);
        response.put("success", success);
        response.put("message", success ? "公告创建成功" : "创建失败");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        Map<String, Object> response = new HashMap<>();
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            response.put("success", false);
            response.put("message", "公告不存在");
            return ResponseEntity.ok(response);
        }
        announcement.setId(id);
        announcement.setUpdateTime(LocalDateTime.now());
        boolean success = announcementService.updateById(announcement);
        response.put("success", success);
        response.put("message", success ? "公告更新成功" : "更新失败");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = announcementService.removeById(id);
        response.put("success", success);
        response.put("message", success ? "公告删除成功" : "删除失败");
        return ResponseEntity.ok(response);
    }
}
