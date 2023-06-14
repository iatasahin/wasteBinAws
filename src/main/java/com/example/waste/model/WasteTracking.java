package com.example.waste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteTracking {
    private Long trackingId;
    private Long typeId;
    private Long wasteBinId;
    private Long userId;
    private String wasteBarcode;
    private Instant time;
}
