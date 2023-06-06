package com.example.waste.controller;

import com.example.waste.exceptions.ValidApiKeyIsRequiredException;
import com.example.waste.dto.TotpRequest;
import com.example.waste.dto.TotpResponse;
import com.example.waste.model.WasteBin;
import com.example.waste.service.TotpService;
import com.example.waste.service.ValidationService;
import com.example.waste.service.FirestoreWasteBinService;

import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.SecretGenerator;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@AllArgsConstructor
public class TotpController {
    private SecretGenerator secretGenerator;
    private ValidationService validationService;
    private TotpService totpService;
    private FirestoreWasteBinService firestoreWasteBinService;

    @GetMapping("/newsecret")
    public ResponseEntity<String> getNewSecret() {
        return ResponseEntity.ok(secretGenerator.generate());
    }

    @GetMapping("/time")
    public ResponseEntity<String> getSecondsAfterEpoch(){
        return ResponseEntity.ok("Time:" + Instant.now().getEpochSecond());
    }

    @GetMapping("/totptest")
    public ResponseEntity<String> getTestTotp(@PathParam("timestamp") int timestamp) throws CodeGenerationException {

        String totp = totpService.generateTotp("QGRMPW2B7T3IJXIPVKSLSSZTWBOSJMGO", timestamp);

        return ResponseEntity.ok("Totp: [" + totp + "]");

    }

    @PostMapping("/totp")
    public ResponseEntity<TotpResponse> createTotp(@RequestBody TotpRequest totpRequest) throws CodeGenerationException {
//        if (! validationService.validate(totpRequest.getApiKey())){
//            throw new ValidApiKeyIsRequiredException();
//        }
        WasteBin wasteBin = firestoreWasteBinService.getWasteBin(totpRequest.getWasteBinId());
        wasteBin.setLastAccessedUserId(Long.parseLong(totpRequest.getUserId()));
        firestoreWasteBinService.createWasteBin(wasteBin);

        TotpResponse totp = totpService.generateTotp(wasteBin);

        return ResponseEntity.ok(totp);
    }
}
