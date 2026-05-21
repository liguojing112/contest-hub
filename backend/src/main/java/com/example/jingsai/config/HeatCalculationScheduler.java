package com.example.jingsai.config;

import com.example.jingsai.entity.Competition;
import com.example.jingsai.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeatCalculationScheduler {

    private final CompetitionService competitionService;

    @Scheduled(fixedRate = 300000)
    public void calculateHeat() {
        List<Competition> competitions = competitionService.list();
        for (Competition c : competitions) {
            long registered = c.getRegistered() != null ? c.getRegistered() : 0;
            long messageCount = c.getMessageCount() != null ? c.getMessageCount() : 0;
            long viewCount = c.getViewCount() != null ? c.getViewCount() : 0;
            double heat = registered * 0.4 + messageCount * 0.3 + viewCount * 0.3;
            c.setHeatValue(Math.round(heat * 100.0) / 100.0);
            competitionService.updateById(c);
        }
        log.debug("热度计算完成，共更新 {} 个竞赛", competitions.size());
    }
}
