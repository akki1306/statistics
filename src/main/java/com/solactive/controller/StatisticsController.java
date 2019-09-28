package com.solactive.controller;

import com.solactive.model.Statistics;
import com.solactive.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private StatisticsService statisticsService;

    StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStastistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/statistics/{instrumentId}")
    public ResponseEntity<Statistics> getStastistics(@PathVariable("instrumentId") String instrumentId) {
        return ResponseEntity.ok(statisticsService.getStatistics(instrumentId));
    }
}
