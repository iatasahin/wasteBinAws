package com.example.waste.controller;

import com.example.waste.exceptions.ValidApiKeyIsRequiredException;
import com.example.waste.model.WasteBin;
import com.example.waste.service.FirestoreWasteBinService;
import com.example.waste.service.ValidationService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@AllArgsConstructor
public class WasteBinController {

    private FirestoreWasteBinService firestoreWasteBinService;
    private ValidationService validationService;

    @PostMapping("/wastebin")
    WasteBin postWasteBin(@RequestBody WasteBin wasteBin, @PathParam("") String api_key) {
        if (!validationService.validate(api_key))
            throw new ValidApiKeyIsRequiredException();
        if (wasteBin.getLastUpdate() == null)
            wasteBin.setLastUpdate(Instant.now());
        return firestoreWasteBinService.createWasteBin(wasteBin);
    }

    @GetMapping("/wastebin/filled")
    List<WasteBin> getFilledWasteBins(@PathParam("") String api_key) {
        if (!validationService.validate(api_key))
            throw new ValidApiKeyIsRequiredException();
        return firestoreWasteBinService.getFilledWasteBins();
    }

    @GetMapping("/wastebin/{id}")
    WasteBin getWasteBin(@PathVariable Long id, @PathParam("") String api_key) {
        if (!validationService.validate(api_key))
            throw new ValidApiKeyIsRequiredException();
        return firestoreWasteBinService.getWasteBin(id);
    }
}
