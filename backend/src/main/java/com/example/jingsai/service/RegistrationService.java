package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Registration;

import java.util.List;

/**
 * 报名Service接口
 */
public interface RegistrationService extends IService<Registration> {

    /**
     * 根据用户ID查询报名记录
     */
    List<Registration> getByUserId(Long userId);

    /**
     * 根据竞赛ID查询报名记录
     */
    List<Registration> getByCompetitionId(Long competitionId);

    /**
     * 检查用户是否已报名某竞赛
     */
    boolean isRegistered(Long userId, Long competitionId);

    /**
     * 创建报名记录
     */
    boolean createRegistration(Long userId, Long competitionId, Long teamId);

    /**
     * 取消报名
     */
    boolean cancelRegistration(Long id);

    /**
     * 更新报名状态
     */
    boolean updateStatus(Long id, String status);
}