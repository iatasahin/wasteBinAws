package com.example.waste.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class WasteBinStatus {
    private WasteBin wasteBin;
    private int fullnessLevel;
    private Instant measurementTime;
}
