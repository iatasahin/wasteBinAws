package com.example.waste.service;

import com.example.waste.exceptions.DistanceNotFoundException;
import com.example.waste.model.Distance;
import com.example.waste.model.Location;
import com.example.waste.repository.DistanceRepository;
import com.example.waste.repository.LocationRepository;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TspDistanceService {
    private GeoApiContext geoApiContext;
    private LocationRepository locationRepository;
    private DistanceRepository distanceRepository;

    public String requestDistances() throws IOException, InterruptedException, ApiException {
        List<Location> allLocations = locationRepository.findAll();
        List<Location> locations = locationRepository.findAllByDistancesAreCalculatedIsFalse();

        for (Location location : locations) {
            persistDistances(location, allLocations);
            persistDistances(allLocations, location);
            location.setDistancesAreCalculated(true);
            locationRepository.save(location);
        }

        int numRequests = 2 * locations.size() * allLocations.size();
        return "Number of requests: " + numRequests + "<br>\n" +
                "Apprx. cost: " + (numRequests / 200.0) + " $";
    }

    private void persistDistances(Location location, List<Location> allLocations)
            throws IOException, InterruptedException, ApiException
    {
        LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng[] destinations = new LatLng[allLocations.size()];

        for (int i = 0; i < allLocations.size(); i++) {
            destinations[i] = new LatLng(
                    allLocations.get(i).getLatitude(),
                    allLocations.get(i).getLongitude()
            );
        }

        long[][] distanceMatrix = distanceMatrix(origin, destinations);

        for (int i = 0; i < allLocations.size(); i++) {
            Optional<Distance> optionalDistance =
                    distanceRepository.findByOriginAndDestination(location, allLocations.get(i));
            if(optionalDistance.isEmpty()){
                distanceRepository.save(new Distance(0L, location, allLocations.get(i), distanceMatrix[0][i]));
            } else {
                Distance distance = optionalDistance.get();
                distance.setDistanceInMeters(distanceMatrix[0][i]);
                distanceRepository.save(distance);
            }
        }
    }

    private long[][] distanceMatrix(LatLng origin, LatLng[] destinations)
            throws IOException, InterruptedException, ApiException
    {
        long[][] result = new long[1][destinations.length];

        for (int offset = 0; offset < destinations.length; offset += 25) {
            int start = offset;
            int end = Math.min(offset + 25, destinations.length);

            DistanceMatrixApiRequest distanceMatrixApiRequest =
                    new DistanceMatrixApiRequest(geoApiContext)
                            .avoid(DirectionsApi.RouteRestriction.FERRIES)
                            .avoid(DirectionsApi.RouteRestriction.TOLLS)
                            .origins(origin)
                            .destinations(Arrays.copyOfRange(destinations, start, end));

            DistanceMatrix distanceMatrix = distanceMatrixApiRequest.await();

            DistanceMatrixRow row = distanceMatrix.rows[0];

            int columnIndex = 0;
            for (DistanceMatrixElement element : row.elements) {
                result[0][offset + columnIndex] = element.distance.inMeters;
                columnIndex++;
            }
        }
        return result;
    }

    private void persistDistances(List<Location> allLocations, Location location)
            throws IOException, InterruptedException, ApiException
    {
        LatLng[] origins = new LatLng[allLocations.size()];
        LatLng destination = new LatLng(location.getLatitude(), location.getLongitude());

        for (int i = 0; i < allLocations.size(); i++) {
            origins[i] = new LatLng(
                    allLocations.get(i).getLatitude(),
                    allLocations.get(i).getLongitude()
            );
        }

        long[][] distanceMatrix = distanceMatrix(origins, destination);

        for (int i = 0; i < allLocations.size(); i++) {
            Optional<Distance> optionalDistance =
                    distanceRepository.findByOriginAndDestination(allLocations.get(i), location);
            if(optionalDistance.isEmpty()){
                distanceRepository.save(new Distance(0L, allLocations.get(i), location, distanceMatrix[i][0]));
            } else {
                Distance distance = optionalDistance.get();
                distance.setDistanceInMeters(distanceMatrix[i][0]);
                distanceRepository.save(distance);
            }
        }
    }

    private long[][] distanceMatrix(LatLng[] origins, LatLng destination) throws IOException, InterruptedException, ApiException {
        long[][] result = new long[origins.length][1];

        for (int offset = 0; offset < origins.length; offset += 25) {
            int start = offset;
            int end = Math.min(offset + 25, origins.length);

            DistanceMatrixApiRequest distanceMatrixApiRequest =
                    new DistanceMatrixApiRequest(geoApiContext)
                            .avoid(DirectionsApi.RouteRestriction.FERRIES)
                            .avoid(DirectionsApi.RouteRestriction.TOLLS)
                            .origins(Arrays.copyOfRange(origins, start, end))
                            .destinations(destination);

            DistanceMatrix distanceMatrix = distanceMatrixApiRequest.await();

            int rowIndex = 0;
            for (DistanceMatrixRow row : distanceMatrix.rows) {
                DistanceMatrixElement element = row.elements[0];
                result[offset + rowIndex][0] = element.distance.inMeters;
                rowIndex++;
            }
        }
        return result;
    }

    long[][] findDistanceMatrix(List<Location> locations) {
        long[][] distanceMatrix = new long[locations.size()][locations.size()];

        for (int iRow = 0; iRow < locations.size(); iRow++) {
            for (int iColumn = 0; iColumn < locations.size(); iColumn++) {
                Optional<Distance> optionalDistance = distanceRepository
                        .findByOriginAndDestination(locations.get(iRow), locations.get(iColumn));
                if(optionalDistance.isEmpty()){
                    throw new DistanceNotFoundException();
                }
                distanceMatrix[iRow][iColumn] = optionalDistance.get().getDistanceInMeters();
            }
        }
        return distanceMatrix;
    }
}
