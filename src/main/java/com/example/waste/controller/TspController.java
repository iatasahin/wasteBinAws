package com.example.waste.controller;

import com.example.waste.dto.SyncResponse;
import com.example.waste.model.Location;
import com.example.waste.repository.LocationRepository;
import com.example.waste.service.TspDistanceService;
import com.example.waste.service.TspPersistenceService;
import com.example.waste.service.TspRouteService;
import com.google.maps.errors.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tsp")
@AllArgsConstructor
public class TspController {
    private TspPersistenceService tspPersistenceService;
    private TspDistanceService tspDistanceService;
    private TspRouteService tspRouteService;
    private LocationRepository locationRepository;

//    @GetMapping("/syncfirebase")
    SyncResponse syncFirebase(){
        return tspPersistenceService.syncFirebase();
    }

//    @GetMapping("/getdistances")
    String requestDistances() throws IOException, InterruptedException, ApiException {
        return tspDistanceService.requestDistances();
    }

    @GetMapping("/route")
    List<Long> getRoute(@RequestParam(required = false) Integer fullness){
        return tspRouteService.findShortestRoute(fullness != null ? fullness : 0);
    }

//    @GetMapping("/startingpoint")
    void setStartingPoint(@RequestParam double latitude, @RequestParam double longitude){
        Optional<Location> optionalLocation = locationRepository.findByFirebaseId(-1L);
        Location location;
        if (optionalLocation.isEmpty()) {
            location = new Location(0L, -1L, latitude, longitude, false);
        } else {
            location = optionalLocation.get();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setDistancesAreCalculated(false);
        }
        locationRepository.save(location);
    }
}
