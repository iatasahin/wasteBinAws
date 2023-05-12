package com.example.waste.controller;

import com.example.waste.dto.SyncResponse;
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

@RestController
@RequestMapping("/tsp")
@AllArgsConstructor
public class TspController {
    private TspPersistenceService tspPersistenceService;
    private TspDistanceService tspDistanceService;
    private TspRouteService tspRouteService;

    @GetMapping("/syncfirebase")
    SyncResponse syncFirebase(){
        return tspPersistenceService.syncFirebase();
    }

    @GetMapping("/getdistances")
    String requestDistances() throws IOException, InterruptedException, ApiException {
        return tspDistanceService.requestDistances();
    }

    @GetMapping("/route")
    List<Long> getRoute(@RequestParam(required = false) Integer fullness){
        return tspRouteService.findShortestRoute(fullness != null ? fullness : 0);
    }
}
