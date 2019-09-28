package com.solactive.util;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateTimeUtil {

    public static final long MILLIS_FOR_ONE_SECOND = 1000;

    public long currentMillis() {
        return Instant.now().toEpochMilli();
    }

    public long convertTimeInMillisToSeconds(long timeInMillis) {
        return timeInMillis / MILLIS_FOR_ONE_SECOND;
    }

    public long currentSeconds() {
        return convertTimeInMillisToSeconds(currentMillis());
    }


}
