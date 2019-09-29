package com.solactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@ToString
public class Tick {
    private String instrument;
    private double price;
    private long timestamp;

}
