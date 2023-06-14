package com.example.waste.service;

import com.example.waste.model.WasteTracking;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class FirestoreWasteTrackingService {
    private Firestore firestore;

    public void createWasteTracking(WasteTracking wasteTracking) {
        try {
            CollectionReference collectionReference = firestore.collection("WasteTracking");
            String previousId = collectionReference
                    .orderBy("trackingID", Query.Direction.DESCENDING).limit(1)
                    .get().get().getDocuments().get(0)
                    .getString("trackingID");
            wasteTracking.setTrackingId(Long.parseLong(previousId) + 1);

            collectionReference.add(wasteTrackingToMap(wasteTracking));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> wasteTrackingToMap(WasteTracking wasteTracking) {
        Map<String, Object> data = new HashMap<>();
        data.put("trackingID", "" + wasteTracking.getTrackingId());
        data.put("typeID", "" + wasteTracking.getTypeId());
        data.put("wasteBinID", "" + wasteTracking.getWasteBinId());
        data.put("userID", "" + wasteTracking.getUserId());
        data.put("wasteBarcode", wasteTracking.getWasteBarcode());
        Timestamp time = Timestamp.of(Date.from(wasteTracking.getTime()));
        data.put("time", time);

        return data;
    }
}
