package com.example.waste.dto;

import com.example.waste.model.RebateImage;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageDetails {
    private UUID imageId;
    private Long wasteBinId;
    private Long userId;
    private String barcode;
    private Instant saved;

    public ImageDetails(RebateImage rebateImage) {
        this(
                rebateImage.getId(),
                rebateImage.getWasteBinId(),
                rebateImage.getUserId(),
                rebateImage.getBarcode(),
                rebateImage.getSaved()
        );
    }
}
