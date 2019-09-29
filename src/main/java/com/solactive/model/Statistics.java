package com.solactive.model;

import lombok.*;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


/**
 * POJO class used to store the statistics. Can be accessed from multiple threads
 * as it uses AtomicReference and AtomicInteger using lock free algorithms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Statistics {
    private AtomicReference<Double> avg = new AtomicReference<>(0.0d);
    private AtomicReference<Double> max = new AtomicReference<>(Double.NEGATIVE_INFINITY);
    private AtomicReference<Double> min = new AtomicReference<>(Double.POSITIVE_INFINITY);
    private AtomicLong count = new AtomicLong(0);
    private AtomicReference<Double> sum = new AtomicReference<>(0.0d);

    public void accept(double value) {
        count.incrementAndGet();
        sum.getAndUpdate(s -> sum.get() + value);
        min.getAndUpdate(m -> Math.min(min.get(), value));
        max.getAndUpdate(m -> Math.max(max.get(), value));
        avg.getAndUpdate(a -> (sum.get() / count.get()));
    }

}
