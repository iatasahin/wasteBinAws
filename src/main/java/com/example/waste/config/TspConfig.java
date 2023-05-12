package com.example.waste.config;

import com.google.maps.GeoApiContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TspConfig {
    @Bean
    GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder()
                .apiKey(System.getenv("GOOGLE_MAPS_API_KEY"))
                .build();
    }
}
