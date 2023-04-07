package com.example.waste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteBin {
    public static final int FILLED_LIMIT = 75;
    private Long id;
    private double latitude;
    private double longitude;
    private int fullnessLevel;
    private Instant lastUpdate;
    private String secretBase32;
}
