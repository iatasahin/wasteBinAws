package com.example.waste.controller;

import com.example.waste.model.WasteBinStatus;
import com.example.waste.service.WasteBinStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@AllArgsConstructor
public class WasteBinStatusController {
    private WasteBinStatusService service;

    @PostMapping("/wastebinstatus")
    WasteBinStatus postWasteBinStatus(@RequestBody WasteBinStatus wasteBinStatus){
        if(wasteBinStatus.getMeasurementTime() == null)
            wasteBinStatus.setMeasurementTime(Instant.now());
        return service.createWasteBinStatus(wasteBinStatus);
    }
}
