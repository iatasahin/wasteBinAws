package com.example.waste.controller;

import com.example.waste.dto.SimulateFullnessRequest;
import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteBinStatus;
import com.example.waste.service.FirestoreWasteBinService;
import com.example.waste.service.SimulationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/simulate/fullness")
    void simulateFullness(@RequestBody SimulateFullnessRequest request) {
        List<WasteBin> allWasteBins = new ArrayList<>(firestoreWasteBinService.getAllWasteBins());
        List<WasteBin> wasteBins;
        Integer numWasteBins = request.getWastebins();
        Random random = new Random();
        if (numWasteBins > 0 && numWasteBins < allWasteBins.size()) {
            wasteBins = new ArrayList<>();
            for (int i = 0; i < numWasteBins; i++) {
                wasteBins.add(allWasteBins.remove(random.nextInt(allWasteBins.size())));
            }
        } else {
            wasteBins = allWasteBins;
        }
        int fullness;
        if (request.getFullness() >= 100) {
            fullness = 100;
        } else if (request.getFullness() <= 0) {
            fullness = 0;
        } else {
            fullness = request.getFullness();
        }
        String rel = request.getRelation();
        if (rel.equals("to") ||
                (rel.equals("above") && fullness == 100) ||
                (rel.equals("below") && fullness == 0)) {
            for (WasteBin wasteBin : wasteBins) {
                wasteBinStatusController.postWasteBinStatus(new WasteBinStatus(
                        wasteBin, fullness, null
                ));
            }
        } else if (request.getRelation().equals("above")) {
            for (WasteBin wasteBin : wasteBins) {
                wasteBinStatusController.postWasteBinStatus(new WasteBinStatus(
                        wasteBin, (fullness + random.nextInt(101 - fullness)), null
                ));
            }
        } else { //"below"
            for (WasteBin wasteBin : wasteBins) {
                wasteBinStatusController.postWasteBinStatus(new WasteBinStatus(
                        wasteBin, (fullness - random.nextInt(fullness + 1)), null
                ));
            }
        }
    }

    @GetMapping("/simulate/collection")
    String simulateCollection(@RequestParam Integer fullnessLimit, @RequestParam Integer collectionTime) {
        return simulationService.collect(fullnessLimit, collectionTime);
    }
}
