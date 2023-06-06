package com.example.waste.service;

import com.example.waste.dto.ImageDetails;
import com.example.waste.model.RebateImage;
import com.example.waste.repository.RebateImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RebateImageService {
    private RebateImageRepository rebateImageRepository;

    @Transactional
    public List<ImageDetails> getImageDetails(Long wasteBinId) {
        List<RebateImage> images = rebateImageRepository.findTop10ByWasteBinIdOrderBySavedDesc(wasteBinId);
        List<ImageDetails> imageDetails = new ArrayList<>();
        for (RebateImage image : images) {
            imageDetails.add(new ImageDetails(image));
        }
        return imageDetails;
    }
}
