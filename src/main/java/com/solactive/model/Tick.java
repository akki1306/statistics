package com.solactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Tick {
    private String instrument;
    private double price;
    private long timestamp;

}
