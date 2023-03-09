package com.example.waste.service;

import com.example.waste.model.WasteBin;
import com.example.waste.model.WasteBinStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class WasteBinService {
    private Firestore firestore;

    public WasteBin createWasteBin(WasteBin wasteBin) {
        DocumentSnapshot document = getWasteBinDocumentById(wasteBin.getId());
        if(document == null){
            Map<String, Object> data = wasteBinToMap(wasteBin);
            firestore.collection("WasteBin").add(data);
        } else {
            DocumentReference documentReference = document.getReference();
            documentReference.set(wasteBinToMap(wasteBin));
        }
        return wasteBin;
    }

    public WasteBin getWasteBin(Long id) {
        return mapToWasteBin(getWasteBinDocumentById(id));
    }

    private DocumentSnapshot getWasteBinDocumentById(Long id){
        Query query = firestore.collection("WasteBin")
                .whereEqualTo("id", "" + id)
                .limit(1);
        try {
            List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();
            if(documents.isEmpty()) return null;
            return documents.get(0);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WasteBin> getFilledWasteBins() {
        Query query = firestore
                .collection("WasteBin")
                .whereGreaterThanOrEqualTo("fullness", WasteBin.FILLED_LIMIT);

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

    DocumentReference updateWasteBin(WasteBinStatus wasteBinStatus) {
        DocumentSnapshot documentSnapshot = getWasteBinDocumentById(wasteBinStatus.getWasteBin().getId());
        WasteBin wasteBin = mapToWasteBin(documentSnapshot);
        wasteBin.setFullnessLevel(wasteBinStatus.getFullnessLevel());
        wasteBin.setLastUpdate(Instant.now());
        wasteBinStatus.setWasteBin(wasteBin);
        createWasteBin(wasteBin);
        return documentSnapshot.getReference();
    }

    private WasteBin mapToWasteBin(DocumentSnapshot data) {
        GeoPoint location = data.getGeoPoint("location");
        Timestamp lastUpdate = data.getTimestamp("lastUpdate");
        return new WasteBin(
                Long.parseLong(data.getString("id")),
                location.getLatitude(),
                location.getLongitude(),
                data.getLong("fullness").intValue(),
                lastUpdate == null ? Instant.EPOCH : lastUpdate.toDate().toInstant()
        );
    }

    private Map<String, Object> wasteBinToMap(WasteBin wasteBin){
        Map<String, Object> data = new HashMap<>();
        data.put("id", "" + wasteBin.getId());
        data.put("fullness", wasteBin.getFullnessLevel());
        GeoPoint location = new GeoPoint(wasteBin.getLatitude(), wasteBin.getLongitude());
        data.put("location", location);
        Timestamp lastUpdateTime = Timestamp.of(Date.from(wasteBin.getLastUpdate()));
        data.put("lastUpdate", lastUpdateTime);

        return data;
    }
}
