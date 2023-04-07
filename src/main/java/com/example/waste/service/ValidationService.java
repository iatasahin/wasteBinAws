package com.example.waste.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidationService {
    List<String> apiKeys;

    public ValidationService() {
        String keys = System.getenv("WASTE_BIN_PROJECT_API_KEYS");
        apiKeys = Arrays.stream(keys.split(":")).toList();
    }

    public boolean validate(String apiKey){
        return apiKeys.contains(apiKey);
    }
}
