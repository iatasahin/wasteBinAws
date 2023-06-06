package com.example.waste.controller;

import com.example.waste.dto.ImageDetails;
import com.example.waste.model.RebateImage;
import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteTracking;
import com.example.waste.repository.RebateImageRepository;
import com.example.waste.service.BarcodeScannerService;

import com.example.waste.service.FirestoreWasteBinService;
import com.example.waste.service.FirestoreWasteTrackingService;
import com.example.waste.service.RebateImageService;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
//@AllArgsConstructor
@RequiredArgsConstructor
public class RebateController {
    private final BarcodeScannerService barcodeScannerService;
    private final FirestoreWasteTrackingService firestoreWasteTrackingService;
    private final FirestoreWasteBinService firestoreWasteBinService;
    private final RebateImageRepository rebateImageRepository;
    private final RebateImageService rebateImageService;

    private Logger logger = Logger.getLogger(getClass().getName());

    @PostMapping("/rebate/barcode")
    ResponseEntity<String> uploadBarcode(@RequestParam("image") MultipartFile file,
                                         @RequestParam("waste_bin") Long wasteBinId)
            throws IOException, FormatException {
        logger.info("uploadBarcode() is called, wasteBinId: " + wasteBinId);

        WasteBin wasteBin = firestoreWasteBinService.getWasteBin(wasteBinId);

        try (InputStream inputStream = file.getInputStream()) {
            String barcode = barcodeScannerService.scan(inputStream);

            WasteTracking wasteTracking = new WasteTracking(
                    null, wasteBinId, wasteBin.getLastAccessedUserId(), barcode, Instant.now()
            );
            firestoreWasteTrackingService.createWasteTracking(wasteTracking);

            rebateImageRepository.save(new RebateImage(
                    null, wasteBinId, wasteBin.getLastAccessedUserId(), barcode, Instant.now(),
                    file.getBytes()
            ));

            logger.info("Barcode : [" + barcode + "]");
            return ResponseEntity.ok("Barcode : [" + barcode + "]");
        } catch (NotFoundException e) {
            rebateImageRepository.save(new RebateImage(
                    null, wasteBinId, wasteBin.getLastAccessedUserId(), "Not Found", Instant.now(),
                    file.getBytes()
            ));

            logger.info("No barcode found in image");
            return ResponseEntity.internalServerError().body("Barcode Not Found");
        }
    }

    @GetMapping("/rebate/images")
    List<ImageDetails> getImageDetails(@RequestParam("waste_bin") Long wasteBinId) {
        return rebateImageService.getImageDetails(wasteBinId);
    }

    @GetMapping("/rebate/image")
    ResponseEntity<byte[]> getImage(@RequestParam UUID uuid) {

        byte[] image = rebateImageRepository.findById(uuid).get().getImage();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
