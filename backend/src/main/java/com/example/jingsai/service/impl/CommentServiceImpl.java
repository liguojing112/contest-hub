package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Comment;
import com.example.jingsai.mapper.CommentMapper;
import com.example.jingsai.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> getByCompetitionId(Long competitionId) {
        return getBaseMapper().selectByCompetitionId(competitionId);
    }

    @Override
    public boolean addComment(Long competitionId, Long userId, String userName, String content, Long parentId) {
        Comment comment = new Comment();
        comment.setCompetitionId(competitionId);
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setCreateTime(LocalDateTime.now());
        return save(comment);
    }

    @Override
    public List<Map<String, Object>> getCommentsWithReplies(Long competitionId) {
        List<Comment> all = getBaseMapper().selectByCompetitionId(competitionId);
        // 分离顶级评论和回复
        List<Map<String, Object>> result = new ArrayList<>();
        Map<Long, List<Map<String, Object>>> replyMap = new HashMap<>();

        for (Comment c : all) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("competitionId", c.getCompetitionId());
            item.put("userId", c.getUserId());
            item.put("userName", c.getUserName());
            item.put("content", c.getContent());
            item.put("parentId", c.getParentId());
            item.put("createTime", c.getCreateTime());

            if (c.getParentId() == null) {
                item.put("replies", new ArrayList<>());
                result.add(item);
            } else {
                replyMap.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(item);
            }
        }

        // 把回复挂到对应评论下
        for (Map<String, Object> comment : result) {
            Long commentId = (Long) comment.get("id");
            List<Map<String, Object>> replies = replyMap.get(commentId);
            if (replies != null) {
                comment.put("replies", replies);
            }
        }

        return result;
    }
}
