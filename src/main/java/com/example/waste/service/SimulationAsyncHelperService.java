package com.example.waste.service;

import com.example.waste.controller.WasteBinStatusController;
import com.example.waste.model.Distance;
import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteBinStatus;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@AllArgsConstructor
public class SimulationAsyncHelperService {
    private WasteBinStatusController wasteBinStatusController;

    @Async
    public void collect(List<Long> route, List<Distance> distances, Integer collectionTime) {
        Long totalDistance = 0L;
        for (Distance distance : distances) {
            totalDistance += distance.getDistanceInMeters();
        }
        Long milisecondPerMeter = collectionTime * 1000 / totalDistance;
        Iterator<Distance> distanceIterator = distances.iterator();
        for (Long id : route) {
            try {
                Thread.sleep(distanceIterator.next().getDistanceInMeters() * milisecondPerMeter);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            WasteBin wasteBin = new WasteBin();
            wasteBin.setId(id);
            wasteBinStatusController.postWasteBinStatus(new WasteBinStatus(
                    wasteBin, 0, null
            ));
        }
    }
}
