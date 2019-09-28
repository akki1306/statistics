package com.solactive.validator;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TickValidator {
    public static final long timeToLive = 60000;

    private TickValidator() {

    }

    public static boolean withinLastMinute(final long lastAccessedTime) {
        final long now = System.currentTimeMillis();
        return (now - lastAccessedTime) < timeToLive;
    }

    public static boolean isInFuture(final Date lastAccessdate) {
        return new Date().before(lastAccessdate);
    }
}
