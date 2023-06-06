package com.example.waste.service;

import com.example.waste.model.Distance;
import com.example.waste.model.Location;
import com.example.waste.repository.DistanceRepository;
import com.example.waste.repository.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SimulationService {
    private TspRouteService tspRouteService;
    private DistanceRepository distanceRepository;
    private final LocationRepository locationRepository;
    private SimulationAsyncHelperService simulationAsyncHelperService;

    public String collect(Integer fullnessLimit, Integer collectionTime) {
        List<Long> route = tspRouteService.findShortestRoute(fullnessLimit);

        List<Distance> distances = new ArrayList<>();
        Long totalDistance = 0L;
        Location previous = locationRepository.findByFirebaseId(-1L).get();
        for (Long id : route) {
            Location current = locationRepository.findByFirebaseId(id).get();
            Distance distance = distanceRepository.findByOriginAndDestination(previous, current).get();
            distances.add(distance);
            totalDistance += distance.getDistanceInMeters();
            previous = current;
        }

        simulationAsyncHelperService.collect(route, distances, collectionTime);

        return "Collection will take " + collectionTime + "seconds" +
                        "<br>\nWaste Bins will be collected: " + route +
                        "<br>\nTotal Distance Between Waste Bins: "  + totalDistance;
    }
}
