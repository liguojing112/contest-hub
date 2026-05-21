package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Appeal;

import java.util.List;

public interface AppealService extends IService<Appeal> {

    List<Appeal> getByStudentId(Long studentId);

    List<Appeal> getByStatus(String status);

    boolean submitAppeal(Long studentId, Long registrationId, String content);

    boolean handleAppeal(Long id, String reply);
}
