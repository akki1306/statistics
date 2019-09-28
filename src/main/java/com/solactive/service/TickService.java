package com.solactive.service;

import com.solactive.model.Statistics;
import com.solactive.model.Tick;

public interface TickService {

    Statistics saveTick(Tick tick);
}
