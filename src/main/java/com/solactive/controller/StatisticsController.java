package com.solactive.controller;

import com.solactive.model.Statistics;
import com.solactive.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StatisticsController {

    private StatisticsService statisticsService;

    StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/app/statistics")
    public ResponseEntity<Statistics> getStastistics() {
        log.debug("Getting statistics for the current time in seconds");
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/app/statistics/{instrumentId}")
    public ResponseEntity<Statistics> getStastistics(@PathVariable("instrumentId") String instrumentId) {
        log.debug("Getting statistics for current time in seconds for instrument id : " + instrumentId);
        return ResponseEntity.ok(statisticsService.getStatistics(instrumentId));
    }
}
