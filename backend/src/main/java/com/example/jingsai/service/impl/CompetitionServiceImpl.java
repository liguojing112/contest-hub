package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Competition;
import com.example.jingsai.entity.User;
import com.example.jingsai.mapper.CompetitionMapper;
import com.example.jingsai.service.CompetitionService;
import com.example.jingsai.service.MessageService;
import com.example.jingsai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final MessageService messageService;
    private final UserService userService;

    @Override
    public List<Competition> getByCreatorId(Long creatorId) {
        return getBaseMapper().selectByCreatorId(creatorId);
    }

    @Override
    public List<Competition> getByStatus(String status) {
        return getBaseMapper().selectByStatus(status);
    }

    @Override
    public List<Competition> getActiveCompetitions() {
        return getBaseMapper().selectActiveCompetitions();
    }

    @Override
    public boolean createCompetition(Competition competition, Long creatorId, String creatorName) {
        competition.setCreatorId(creatorId);
        competition.setCreator(creatorName);
        boolean isDraft = "draft".equals(competition.getStatus());
        if (!isDraft) {
            competition.setStatus("pending");
        }
        competition.setRegistered(0);
        competition.setCreateTime(LocalDateTime.now());
        competition.setUpdateTime(LocalDateTime.now());
        boolean success = save(competition);
        if (success && !isDraft) {
            List<User> admins = userService.getByRole("ADMIN");
            for (User admin : admins) {
                messageService.sendUserMessage(admin.getId(), creatorId, creatorName,
                        "教师 " + creatorName + " 创建了新竞赛「" + competition.getName() + "」，请审核");
            }
        }
        return success;
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Competition competition = getById(id);
        if (competition != null) {
            competition.setStatus(status);
            competition.setUpdateTime(LocalDateTime.now());
            return updateById(competition);
        }
        return false;
    }

    @Override
    public List<Competition> getByReviewer(String reviewerName) {
        User user = userService.getByUsername(reviewerName);
        String nameToMatch = user != null ? user.getName() : reviewerName;
        List<Competition> all = list();
        return all.stream()
                .filter(c -> c.getReviewers() != null && c.getReviewers().contains(nameToMatch))
                .toList();
    }
}