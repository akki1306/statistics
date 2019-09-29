package com.solactive.controller;

import com.solactive.model.Tick;
import com.solactive.service.TickService;
import com.solactive.validator.TickValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Controller to store the tick data received.
 */
@RestController
@Slf4j
public class TickController {

    private TickService tickService;

    TickController(TickService tickService) {
        this.tickService = tickService;
    }

    @PostMapping("/app/tick")
    public ResponseEntity addTransaction(@RequestBody Tick tick) {
        Date lastAccessdate = new Date(tick.getTimestamp());
        if (TickValidator.isInFuture(lastAccessdate)) {
            log.debug("Expired tick received " + tick);
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (TickValidator.withinLastMinute(lastAccessdate.getTime())) {
            log.debug("Saving tick " + tick);
            tickService.saveTick(tick);
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

}
