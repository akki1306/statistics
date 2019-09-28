package com.solactive.service.impl;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.StatisticsService;
import com.solactive.store.Store;
import com.solactive.util.TimeUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
            return statistics.get();
        } else {
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
            return statistics.get();
        } else {
            return new Statistics();
        }
    }

    /**
     * @param tick
     * @return save the incoming transaction data and calculate the updated stats summary
     * At the same time, update the store with the new tick
     */
    public Statistics updateStatistics(Tick tick) {
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

        store.removeFromStore(oldestTimeInSeconds);
    }

}
