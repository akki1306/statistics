package com.solactive.service.impl;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;
import com.solactive.service.TickService;
import com.solactive.store.Store;
import org.springframework.stereotype.Service;

@Service
public class TickServiceImpl implements TickService {
    private Store store;

    TickServiceImpl(Store store) {
        this.store = store;
    }

    @Override
    public Statistics saveTick(Tick tick) {
        return store.updateStatistics(tick.getInstrument(), tick.getTimestamp(), tick.getPrice());
    }
}
