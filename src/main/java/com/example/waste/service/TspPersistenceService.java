package com.example.waste.service;

import com.example.waste.dto.SyncResponse;
import com.example.waste.exceptions.LocationNotFoundException;
import com.example.waste.model.Location;
import com.example.waste.model.WasteBin;
import com.example.waste.repository.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TspPersistenceService {
    private FirestoreWasteBinService firestoreWasteBinService;
    private LocationRepository locationRepository;

    public SyncResponse syncFirebase() {
        int added = 0;
        int changed = 0;
        int needsDistanceCalculation = 0;
        List<WasteBin> wasteBins = firestoreWasteBinService.getAllWasteBins();
        for (WasteBin w : wasteBins) {
            Optional<Location> optional = locationRepository.findByFirebaseId(w.getId());
            if (optional.isEmpty()) {
                added++;
                needsDistanceCalculation++;
                locationRepository.save(new Location(
                        0L,
                        w.getId(),
                        w.getLatitude(),
                        w.getLongitude(),
                        false
                ));
            } else {
                Location location = optional.get();
                if (location.getLatitude() != w.getLatitude() || location.getLongitude() != w.getLongitude()) {
                    changed++;
                    location.setDistancesAreCalculated(false);
                    location.setLatitude(w.getLatitude());
                    location.setLongitude(w.getLongitude());
                    locationRepository.save(location);
                }
                if (!location.isDistancesAreCalculated()) {
                    needsDistanceCalculation++;
                }
            }
        }
        int numRequests = 2 * needsDistanceCalculation * wasteBins.size();
        return new SyncResponse(added, changed, needsDistanceCalculation, numRequests, numRequests / 200.0);
    }

    List<Location> findLocations(List<WasteBin> filledWasteBins) {
        List<Location> locations = new ArrayList<>();

        locations.add(locationRepository.findByFirebaseId(-1L).get());
        for (WasteBin w : filledWasteBins) {
            Optional<Location> optionalLocation = locationRepository.findByFirebaseId(w.getId());
            if (optionalLocation.isEmpty()) {
                throw new LocationNotFoundException();
            }
            locations.add(optionalLocation.get());
        }
        return locations;
    }
}
