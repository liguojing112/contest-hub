package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Registration;
import com.example.jingsai.mapper.RegistrationMapper;
import com.example.jingsai.service.RegistrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    @Override
    public List<Registration> getByUserId(Long userId) {
        return getBaseMapper().selectByUserId(userId);
    }

    @Override
    public List<Registration> getByCompetitionId(Long competitionId) {
        return getBaseMapper().selectByCompetitionId(competitionId);
    }

    @Override
    public boolean isRegistered(Long userId, Long competitionId) {
        return getBaseMapper().countByUserAndCompetition(userId, competitionId) > 0;
    }

    @Override
    public boolean createRegistration(Long userId, Long competitionId, Long teamId) {
        if (isRegistered(userId, competitionId)) {
            return false;
        }
        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setCompetitionId(competitionId);
        registration.setTeamId(teamId);
        registration.setStatus("pending");
        registration.setRegisterTime(LocalDateTime.now());
        return save(registration);
    }

    @Override
    public boolean cancelRegistration(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Registration registration = getById(id);
        if (registration != null) {
            registration.setStatus(status);
            return updateById(registration);
        }
        return false;
    }
}