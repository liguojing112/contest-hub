package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Review;

import java.util.List;

public interface ReviewService extends IService<Review> {

    List<Review> getByReviewerId(Long reviewerId);

    List<Review> getByCompetitionId(Long competitionId);

    Review getByRegistrationId(Long registrationId);

    boolean submitReview(Long competitionId, Long registrationId, Long reviewerId, Integer score, String comment);

    boolean updateReview(Long id, Integer score, String comment);

    boolean assignReviewer(Long competitionId, Long registrationId, Long reviewerId);

    List<Review> autoAssignReviewers(Long competitionId, List<Long> reviewerIds);

    List<Review> getPendingByCompetitionId(Long competitionId);
}
