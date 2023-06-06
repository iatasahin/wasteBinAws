package com.example.waste.controller;

import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteBinStatus;
import com.example.waste.service.FirestoreWasteBinService;
import com.example.waste.service.SimulationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@AllArgsConstructor
public class SimulationController {
    private WasteBinStatusController wasteBinStatusController;
    private FirestoreWasteBinService firestoreWasteBinService;
    private SimulationService simulationService;

    @GetMapping("/simulate/fullness")
    void simulateFullness() {
        Random random = new Random();
        List<WasteBin> wasteBins = firestoreWasteBinService.getAllWasteBins();
        for (WasteBin wasteBin : wasteBins) {
            wasteBinStatusController.postWasteBinStatus(new WasteBinStatus(
                    wasteBin, random.nextInt(100), null
            ));
        }
    }

    @GetMapping("/simulate/collection")
    String simulateCollection(@RequestParam Integer fullnessLimit, @RequestParam Integer collectionTime) {
        return simulationService.collect(fullnessLimit, collectionTime);
    }
}
