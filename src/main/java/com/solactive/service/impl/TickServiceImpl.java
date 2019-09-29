package com.solactive.service.impl;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.StatisticsService;
import com.solactive.service.TickService;
import org.springframework.stereotype.Service;

@Service
public class TickServiceImpl implements TickService {
    private StatisticsService statisticsService;

    public TickServiceImpl(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public Statistics saveTick(Tick tick) {
        return statisticsService.updateStatistics(tick);
    }
}
