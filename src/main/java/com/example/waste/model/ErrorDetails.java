package com.example.waste.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDetails {
    @JsonProperty("error_message")
    private String message;
}
