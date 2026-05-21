package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Competition;

import java.util.List;

/**
 * 竞赛Service接口
 */
public interface CompetitionService extends IService<Competition> {

    /**
     * 根据创建者ID查询竞赛列表
     */
    List<Competition> getByCreatorId(Long creatorId);

    /**
     * 根据状态查询竞赛列表
     */
    List<Competition> getByStatus(String status);

    /**
     * 查询活跃竞赛
     */
    List<Competition> getActiveCompetitions();

    /**
     * 创建竞赛
     */
    boolean createCompetition(Competition competition, Long creatorId, String creatorName);

    /**
     * 更新竞赛状态
     */
    boolean updateStatus(Long id, String status);

    /**
     * 根据评审教师名称查询竞赛列表
     */
    List<Competition> getByReviewer(String reviewerName);
}
