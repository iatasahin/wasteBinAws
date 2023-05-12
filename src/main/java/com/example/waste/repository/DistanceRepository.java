package com.example.waste.repository;

import com.example.waste.model.Distance;
import com.example.waste.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistanceRepository extends JpaRepository<Distance, Long> {
    Optional<Distance> findByOriginAndDestination(Location origin, Location destination);
}
