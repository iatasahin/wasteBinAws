package com.example.waste.controller;

import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteBinStatus;
import com.example.waste.service.FirestoreWasteBinService;
import com.example.waste.service.FirestoreWasteBinStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@RestController
@AllArgsConstructor
public class WasteBinStatusController {
    private FirestoreWasteBinStatusService firestoreWasteBinStatusService;
    private FirestoreWasteBinService firestoreWasteBinService;

    @PostMapping("/wastebinstatus")
    WasteBinStatus postWasteBinStatus(@RequestBody WasteBinStatus wasteBinStatus) {
        if (wasteBinStatus.getMeasurementTime() == null)
            wasteBinStatus.setMeasurementTime(Instant.now());
        return firestoreWasteBinStatusService.createWasteBinStatus(wasteBinStatus);
    }

    @GetMapping("/simulate/fullness")
    void simulateFullness() {
        Random random = new Random();
        List<WasteBin> wasteBins = firestoreWasteBinService.getAllWasteBins();
        for (WasteBin wasteBin : wasteBins) {
            postWasteBinStatus(new WasteBinStatus(
                    new WasteBin(wasteBin.getId(), 0L, 0L, 0, null, "", false),
                    random.nextInt(100), null
            ));
        }
    }
}
