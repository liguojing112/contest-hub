package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Appeal;
import com.example.jingsai.mapper.AppealMapper;
import com.example.jingsai.service.AppealService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements AppealService {

    @Override
    public List<Appeal> getByStudentId(Long studentId) {
        return getBaseMapper().selectByStudentId(studentId);
    }

    @Override
    public List<Appeal> getByStatus(String status) {
        return getBaseMapper().selectByStatus(status);
    }

    @Override
    public boolean submitAppeal(Long studentId, Long registrationId, String content) {
        Appeal appeal = new Appeal();
        appeal.setStudentId(studentId);
        appeal.setRegistrationId(registrationId);
        appeal.setContent(content);
        appeal.setStatus("pending");
        appeal.setCreateTime(LocalDateTime.now());
        return save(appeal);
    }

    @Override
    public boolean handleAppeal(Long id, String reply) {
        Appeal appeal = getById(id);
        if (appeal != null) {
            appeal.setReply(reply);
            appeal.setStatus("resolved");
            appeal.setReplyTime(LocalDateTime.now());
            return updateById(appeal);
        }
        return false;
    }
}
