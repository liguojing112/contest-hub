package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Announcement;
import com.example.jingsai.mapper.AnnouncementMapper;
import com.example.jingsai.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}
