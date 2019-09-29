package com.solactive.service.impl;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.StatisticsService;
import com.solactive.store.Store;
import com.solactive.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private Store store;
    private TimeUtil timeUtil;

    public StatisticsServiceImpl(Store store, TimeUtil timeUtil) {
        this.store = store;
        this.timeUtil = timeUtil;
    }

    @Override
    public Statistics getStatistics() {
        Optional<Statistics> statistics = store.get(timeUtil.currentMillis());
        if (statistics.isPresent()) {
            log.debug("Statistics found for current time in seconds " + timeUtil.currentSeconds());
            return statistics.get();
        } else {
            log.debug("Statistics not found for current time in seconds " + timeUtil.currentSeconds());
            Statistics s = new Statistics();
            s.getMax().set(0.0d);
            s.getMin().set(0.0d);
            return s;
        }
    }

    @Override
    public Statistics getStatistics(String instrumentId) {
        Optional<Statistics> statistics = store.get(instrumentId, timeUtil.currentMillis());
        if (statistics.isPresent()) {
            log.debug("Statistics found for current time in seconds " + timeUtil.currentSeconds() + " and instrument id " + instrumentId);
            return statistics.get();
        } else {
            log.debug("Statistics not found for current time in seconds " + timeUtil.currentSeconds() + " and instrument id " + instrumentId);
            Statistics s = new Statistics();
            s.getMax().set(0.0d);
            s.getMin().set(0.0d);
            return s;
        }
    }

    /**
     * @param tick
     * @return save the incoming transaction data and calculate the updated stats summary
     * At the same time, update the store with the new tick
     */
    public Statistics updateStatistics(Tick tick) {
        log.debug("Updating statistics in store for tick " + tick);
        return store.updateStatistics(tick.getInstrument(), timeUtil.convertTimeInMillisToSeconds(tick.getTimestamp()), tick.getPrice());
    }

    /**
     * @return this is a scheduled job which runs every second to clean/prune ticks which are older than 60 seconds
     * from the current time. This is a async task which runs in a separate thread.
     */
    @Async
    @Scheduled(fixedDelay = TimeUtil.MILLIS_FOR_ONE_SECOND, initialDelay = TimeUtil.MILLIS_FOR_ONE_SECOND)
    public void cleanOldStatsPerSecond() {
        long nowInSeconds = timeUtil.currentSeconds();
        long oldestTimeInSeconds = nowInSeconds - 60;
        log.debug("Cleaning statistics from store for time in seconds " + nowInSeconds);
        store.removeFromStore(oldestTimeInSeconds);
    }

}
