package com.solactive.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Tick {
    private String instrument;
    private double price;
    private long timestamp;

}
