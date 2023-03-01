package com.example.waste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteBin {
    private Long id;
    private double latitude;
    private double longitude;
    private boolean filled;
    private int fullnessLevel;
    private Instant lastUpdate;
}
