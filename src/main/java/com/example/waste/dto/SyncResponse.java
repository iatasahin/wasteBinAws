package com.example.waste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SyncResponse {
    private int added;
    private int changed;

    @JsonProperty("needs_distance_calculation")
    private int needsDistanceCalculation;

    @JsonProperty("number_of_required_requests")
    private int requests;

    @JsonProperty("approximate_price($)")
    private double price;
}
