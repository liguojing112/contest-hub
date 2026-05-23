package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {

    List<Comment> getByCompetitionId(Long competitionId);

    boolean addComment(Long competitionId, Long userId, String userName, String content, Long parentId);

    List<Map<String, Object>> getCommentsWithReplies(Long competitionId);
}
