package com.example.waste.service;

import com.example.waste.model.WasteBinStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class WasteBinStatusService {
    private Firestore firestore;
    private WasteBinService wasteBinService;

    public WasteBinStatus createWasteBinStatus(WasteBinStatus wasteBinStatus){
        wasteBinService.updateWasteBin(wasteBinStatus);

        DocumentReference documentReference = firestore
                .collection("bins")
                .document("waste_bin_%03d".formatted(wasteBinStatus.getWasteBin().getId()))
                .collection("bin_history")
                .document("fullness_" + wasteBinStatus.getMeasurementTime());

        Map<String, Object> data = new HashMap<>();
        data.put("fullnessLevel", wasteBinStatus.getFullnessLevel());
        data.put("measurementTime", wasteBinStatus.getMeasurementTime());

        ApiFuture<WriteResult> result = documentReference.set(data);
        try{
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return wasteBinStatus;
    }
}
