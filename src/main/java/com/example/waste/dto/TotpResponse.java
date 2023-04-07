package com.example.waste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotpResponse {
    private String totp;

    @JsonProperty("waste_bin_id")
    private Long wasteBinId;
}
