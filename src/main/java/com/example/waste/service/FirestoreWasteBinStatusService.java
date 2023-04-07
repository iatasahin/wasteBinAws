package com.example.waste.service;

import com.example.waste.model.WasteBinStatus;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class FirestoreWasteBinStatusService {
    private FirestoreWasteBinService firestoreWasteBinService;

    public WasteBinStatus createWasteBinStatus(WasteBinStatus wasteBinStatus){
        CollectionReference statusHistoryCollection = firestoreWasteBinService
                .updateWasteBin(wasteBinStatus)
                .collection("BinHistory");
        Map<String, Object> data = new HashMap<>();
        data.put("fullness", wasteBinStatus.getFullnessLevel());
        data.put("measurementTime", Timestamp.of(Date.from(wasteBinStatus.getMeasurementTime())));
        statusHistoryCollection.add(data);
        return wasteBinStatus;
    }
}
