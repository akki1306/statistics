package com.solactive.store;

import com.solactive.model.Statistics;
import com.solactive.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Store {

    private final TimeUtil timeUtil;

    public Store(TimeUtil timeUtil) {
        this.timeUtil = timeUtil;
    }

    /**
     * concurrent hash map to store statistics for every second
     */
    private ConcurrentHashMap<Long, Statistics> perSecondStatsMap = new ConcurrentHashMap<>();

    /**
     * concurrent hash map to store statistics for each instrument per second.
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Long, Statistics>> perInstrumentMap = new ConcurrentHashMap<>();

    /**
     * @param timeKey
     * @param value
     * @return save the incoming stats if the timeKey is already present in the statsBucket, else create a new bucket
     */
    public Statistics updateStatistics(String instrumentId, Long timeKey, Double value) {

        Statistics statistics = get(timeKey)
                .map(existingPerSecStat -> {
                    existingPerSecStat.accept(value);
                    return existingPerSecStat;
                })
                .orElseGet(() -> {
                    Statistics newPerSecondStatistic = new Statistics();
                    newPerSecondStatistic.accept(value);
                    return newPerSecondStatistic;
                });

        perSecondStatsMap.put(timeKey, statistics);

        Statistics statisticsPerInstrument = get(instrumentId, timeKey)
                .map(existingPerSecInstrumentStats -> {
                    existingPerSecInstrumentStats.accept(value);
                    return existingPerSecInstrumentStats;
                }).orElseGet(() -> {
                    Statistics newPerSecondInstrumentStatistics = new Statistics();
                    newPerSecondInstrumentStatistics.accept(value);
                    return newPerSecondInstrumentStatistics;
                });

        perInstrumentMap.computeIfAbsent(instrumentId, (map -> new ConcurrentHashMap<>())).put(timeKey, statisticsPerInstrument);
        return statistics;
    }

    /**
     * return statistics across all instruments for the input time stamp
     *
     * @param timeKey input timestamp in seconds
     * @return Statistics for the input timestamp
     */
    public Optional<Statistics> get(Long timeKey) {
        return Optional.ofNullable(perSecondStatsMap.get(timeKey));
    }

    /**
     * get statistics for an instrument for the input timestamp.
     *
     * @param instrumentId instrument id for which the statistics is requested.
     * @param timeKey      timestamp in seconds for which statistics is requested.
     * @return
     */
    public Optional<Statistics> get(String instrumentId, Long timeKey) {
        ConcurrentHashMap<Long, Statistics> instrumentStatistics = Optional.ofNullable(perInstrumentMap.get(instrumentId)).orElseGet(() -> new ConcurrentHashMap());
        return Optional.ofNullable(instrumentStatistics.get(timeKey));
    }

    /**
     * removes old data from the store
     */
    public void removeOldEntriesFromStore() {
        long timeKey = timeUtil.currentSeconds();
        perSecondStatsMap.forEach((aLong, statistics) -> {
            if (aLong < timeKey) {
                perSecondStatsMap.remove(aLong);
            }
        });
        perInstrumentMap.forEach((s, longStatisticsConcurrentHashMap) -> longStatisticsConcurrentHashMap.forEach((aLong, statistics) -> {
            if (aLong < timeKey) {
                longStatisticsConcurrentHashMap.remove(aLong);
            }
        }));
    }
}
