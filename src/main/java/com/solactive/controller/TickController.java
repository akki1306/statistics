package com.solactive.controller;

import com.solactive.model.Tick;
import com.solactive.service.TickService;
import com.solactive.validator.TickValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Controller to store the tick data received.
 */
@RestController
public class TickController {

    private TickService tickService;

    TickController(TickService tickService) {
        this.tickService = tickService;
    }

    @PostMapping("/app/tick")
    public ResponseEntity addTransaction(@RequestBody Tick tick) {
        Date lastAccessdate = new Date(tick.getTimestamp());
        if (TickValidator.isInFuture(lastAccessdate)) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (TickValidator.withinLastMinute(lastAccessdate.getTime())) {
            tickService.saveTick(tick);
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}
