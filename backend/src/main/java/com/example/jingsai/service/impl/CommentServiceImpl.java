package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Comment;
import com.example.jingsai.mapper.CommentMapper;
import com.example.jingsai.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> getByCompetitionId(Long competitionId) {
        return getBaseMapper().selectByCompetitionId(competitionId);
    }

    @Override
    public boolean addComment(Long competitionId, Long userId, String userName, String content) {
        Comment comment = new Comment();
        comment.setCompetitionId(competitionId);
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        return save(comment);
    }
}
