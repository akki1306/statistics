package com.solactive.service;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;

public interface StatisticsService {

    Statistics getStatistics();

    Statistics getStatistics(String instrumentId);

    Statistics updateStatistics(Tick tick);

    void cleanOldStatsPerSecond();
}
