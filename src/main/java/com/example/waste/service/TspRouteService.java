package com.example.waste.service;

import com.example.waste.model.Location;
import com.example.waste.model.WasteBin;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
// https://developers.google.com/optimization/routing/tsp#complete_programs
public class TspRouteService {
    private static final int SEARCH_TIME_IN_SECONDS = 1;
    private FirestoreWasteBinService firestoreWasteBinService;
    private TspPersistenceService tspPersistenceService;
    private TspDistanceService tspDistanceService;

    public TspRouteService(FirestoreWasteBinService firestoreWasteBinService, TspPersistenceService tspPersistenceService, TspDistanceService tspDistanceService) {
        this.firestoreWasteBinService = firestoreWasteBinService;
        this.tspPersistenceService = tspPersistenceService;
        this.tspDistanceService = tspDistanceService;
    }

    @PostConstruct
    private void init() {
//        Loader.loadNativeLibraries(); // does not work with fatjar produced by spring-boot maven plugin,
//        native libs are loaded in the next line manually
        System.load(System.getenv("ortools_linux_folder") + "libjniortools.so");
    }

    public List<Long> findShortestRoute(int fullness) {
//        List<WasteBin> filledWasteBins = firestoreWasteBinService.getFilledWasteBins();
        List<WasteBin> filledWasteBins = firestoreWasteBinService.getWasteBinsByFullness(fullness);
        List<Location> locations = tspPersistenceService.findLocations(filledWasteBins);
        long[][] distanceMatrix = tspDistanceService.findDistanceMatrix(locations);
        List<Integer> locationRouteByIndex = solveTsp(distanceMatrix);
        List<Long> locationRouteByWasteBinId = indexToId(locations, locationRouteByIndex);
        return locationRouteByWasteBinId;
    }

    private List<Long> indexToId(List<Location> locations, List<Integer> locationRouteByIndex) {
        List<Long> result = new ArrayList<>();

        for (int i : locationRouteByIndex) {
            result.add(locations.get(i).getFirebaseId());
        }

        return result;
    }

    private static class DataModel {
        public DataModel(long[][] distanceMatrix) {
            this(distanceMatrix, 1, 0);
        }

        public DataModel(long[][] distanceMatrix, int vehicleNumber, int depot) {
            this.distanceMatrix = distanceMatrix;
            this.vehicleNumber = vehicleNumber;
            this.depot = depot;
        }

        public final long[][] distanceMatrix;
        public final int vehicleNumber;
        public final int depot;
    }

    private List<Integer> solveTsp(long[][] distanceMatrix) {
        DataModel data = new DataModel(distanceMatrix);

        RoutingIndexManager manager =
                new RoutingIndexManager(data.distanceMatrix.length, data.vehicleNumber, data.depot);

        RoutingModel routing = new RoutingModel(manager);

        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    // Convert from routing variable Index to user NodeIndex.
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return data.distanceMatrix[fromNode][toNode];
                });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        RoutingSearchParameters searchParameters =
                main
                        .defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(SEARCH_TIME_IN_SECONDS).build())
//                        .setLogSearch(true) // prints search logs to console when true
                        .build();

        Assignment solution = routing.solveWithParameters(searchParameters);

        return solutionToList(routing, manager, solution);
    }

    private List<Integer> solutionToList(RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
        List<Integer> result = new ArrayList<>();

        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            result.add(manager.indexToNode(index));
            index = solution.value(routing.nextVar(index));
        }
        result.add(manager.indexToNode(routing.end(0)));
        return result;
    }
}
