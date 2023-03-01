package com.example.waste.service;

import com.example.waste.model.WasteBin;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class WasteBinService {
    private Firestore firestore;

    public WasteBin createWasteBin(WasteBin wasteBin){
        DocumentReference documentReference = firestore
                .collection("bins")
                .document("waste_bin_%03d".formatted(wasteBin.getId()));
        Map<String, Object> data = new HashMap<>();
        data.put("id", wasteBin.getId());
        data.put("filled", wasteBin.isFilled());
        data.put("fullnessLevel", wasteBin.getFullnessLevel());
        GeoPoint location = new GeoPoint(wasteBin.getLatitude(), wasteBin.getLongitude());
        data.put("location", location);
        Timestamp lastUpdateTime = Timestamp.of(Date.from(wasteBin.getLastUpdate()));
        data.put("lastUpdate", lastUpdateTime);

        ApiFuture<WriteResult> result = documentReference.set(data);
        try{
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return wasteBin;
    }

    public WasteBin getWasteBin(Long id) {
        DocumentReference documentReference = firestore
                .collection("bins")
                .document("waste_bin_%03d".formatted(id));
        ApiFuture<DocumentSnapshot> result = documentReference.get();
        try
        {
            DocumentSnapshot data = result.get(10, TimeUnit.SECONDS);
            return mapToWasteBin(data);
        }
        catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WasteBin> getFilledWasteBins(){
        Query query = firestore
                .collection("bins")
                .whereEqualTo("filled", true);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            return querySnapshot.get()
                    .getDocuments()
                    .stream()
                    .map(this::mapToWasteBin)
                    .toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private WasteBin mapToWasteBin(DocumentSnapshot data){
        GeoPoint location = data.getGeoPoint("location");
        return new WasteBin(
                data.getLong("id"),
                location.getLatitude(),
                location.getLongitude(),
                data.getBoolean("filled"),
                data.getLong("fullnessLevel").intValue(),
                data.getTimestamp("lastUpdate").toDate().toInstant()
        );
    }
}
