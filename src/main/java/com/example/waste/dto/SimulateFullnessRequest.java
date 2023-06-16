package com.example.waste.dto;

import lombok.Data;

@Data
public class SimulateFullnessRequest {
    private String relation;
    private Integer fullness;
    private Integer wastebins;
}
