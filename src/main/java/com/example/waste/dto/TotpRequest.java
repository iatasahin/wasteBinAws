package com.example.waste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TotpRequest {
    @JsonProperty("api_key")
    String apiKey;
    @JsonProperty("waste_bin_id")
    Long wasteBinId;
    @JsonProperty("user_id")
    String userId;
}
