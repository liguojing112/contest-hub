package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Competition;
import com.example.jingsai.entity.Registration;
import com.example.jingsai.entity.Review;
import com.example.jingsai.mapper.CompetitionMapper;
import com.example.jingsai.mapper.ReviewMapper;
import com.example.jingsai.service.MessageService;
import com.example.jingsai.service.RegistrationService;
import com.example.jingsai.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final RegistrationService registrationService;
    private final MessageService messageService;
    private final CompetitionMapper competitionMapper;

    @Override
    public List<Review> getByReviewerId(Long reviewerId) {
        return getBaseMapper().selectByReviewerId(reviewerId);
    }

    @Override
    public List<Review> getByCompetitionId(Long competitionId) {
        return getBaseMapper().selectByCompetitionId(competitionId);
    }

    @Override
    public Review getByRegistrationId(Long registrationId) {
        return getBaseMapper().selectByRegistrationId(registrationId);
    }

    @Override
    public boolean submitReview(Long competitionId, Long registrationId, Long reviewerId, Integer score, String comment) {
        Review existing = getBaseMapper().selectByRegistrationId(registrationId);
        boolean success;
        if (existing != null) {
            existing.setScore(score);
            existing.setComment(comment);
            existing.setReviewTime(LocalDateTime.now());
            existing.setStatus("completed");
            success = updateById(existing);
        } else {
            Review review = new Review();
            review.setCompetitionId(competitionId);
            review.setRegistrationId(registrationId);
            review.setReviewerId(reviewerId);
            review.setScore(score);
            review.setComment(comment);
            review.setStatus("completed");
            review.setReviewTime(LocalDateTime.now());
            success = save(review);
        }

        if (success) {
            Registration reg = registrationService.getById(registrationId);
            if (reg != null) {
                Competition comp = competitionMapper.selectById(competitionId);
                String compName = comp != null ? comp.getName() : "未知竞赛";
                messageService.sendSystemMessage(reg.getUserId(),
                    "你在竞赛「" + compName + "」中的作品已评审完成，得分为 " + score + " 分，请查看评审结果。");
            }
        }
        return success;
    }

    @Override
    public boolean updateReview(Long id, Integer score, String comment) {
        Review review = getById(id);
        if (review != null) {
            review.setScore(score);
            review.setComment(comment);
            review.setReviewTime(LocalDateTime.now());
            return updateById(review);
        }
        return false;
    }

    @Override
    public boolean assignReviewer(Long competitionId, Long registrationId, Long reviewerId) {
        Review existing = getBaseMapper().selectByRegistrationId(registrationId);
        boolean success;
        if (existing != null) {
            existing.setReviewerId(reviewerId);
            existing.setStatus("pending");
            success = updateById(existing);
        } else {
            Review review = new Review();
            review.setCompetitionId(competitionId);
            review.setRegistrationId(registrationId);
            review.setReviewerId(reviewerId);
            review.setStatus("pending");
            review.setReviewTime(LocalDateTime.now());
            success = save(review);
        }
        if (success) {
            Competition comp = competitionMapper.selectById(competitionId);
            String compName = comp != null ? comp.getName() : "未知竞赛";
            messageService.sendUserMessage(reviewerId, null, "系统",
                "您被分配为竞赛「" + compName + "」的评审教师，请前往评审管理页面查看并完成评审。");
        }
        return success;
    }

    @Override
    public List<Review> autoAssignReviewers(Long competitionId, List<Long> reviewerIds) {
        List<Registration> registrations = registrationService.getByCompetitionId(competitionId);
        List<Review> assigned = new ArrayList<>();
        Competition comp = competitionMapper.selectById(competitionId);
        String compName = comp != null ? comp.getName() : "未知竞赛";
        int idx = 0;
        for (Registration reg : registrations) {
            Review existing = getBaseMapper().selectByRegistrationId(reg.getId());
            if (existing != null) continue;
            Long reviewerId = reviewerIds.get(idx % reviewerIds.size());
            Review review = new Review();
            review.setCompetitionId(competitionId);
            review.setRegistrationId(reg.getId());
            review.setReviewerId(reviewerId);
            review.setStatus("pending");
            review.setReviewTime(LocalDateTime.now());
            save(review);
            assigned.add(review);
            messageService.sendUserMessage(reviewerId, null, "系统",
                "您被分配为竞赛「" + compName + "」的评审教师，请前往评审管理页面查看并完成评审。");
            idx++;
        }
        return assigned;
    }

    @Override
    public List<Review> getPendingByCompetitionId(Long competitionId) {
        return getBaseMapper().selectPendingByCompetitionId(competitionId);
    }
}
