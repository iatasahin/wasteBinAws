package com.example.waste.repository;

import com.example.waste.model.RebateImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RebateImageRepository extends JpaRepository<RebateImage, UUID> {

    List<RebateImage> findTop10ByWasteBinIdOrderBySavedDesc(Long wasteBinId);
}
