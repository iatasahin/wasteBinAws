package com.example.waste.service;

import com.example.waste.dto.TotpResponse;
import com.example.waste.model.WasteBin;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class TotpService {

    private CodeGenerator codeGenerator;

    public String generateTotp(String secret, long secondsAfterEpoch) throws CodeGenerationException {
        return codeGenerator.generate(secret, secondsAfterEpoch / 30);
    }

    public TotpResponse generateTotp(WasteBin wasteBin) throws CodeGenerationException {
        return new TotpResponse(
                generateTotp(
                        wasteBin.getSecretBase32(),
                        Instant.now().getEpochSecond()
                ), wasteBin.getId()
        );
    }
}
