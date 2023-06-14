package com.example.waste.service;

import com.example.waste.dto.TotpResponse;
import com.example.waste.model.WasteBin;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TotpService {

    private CodeGenerator codeGenerator;

    public String generateTotp(String secret, long secondsAfterEpoch) throws CodeGenerationException {
        return codeGenerator.generate(secret, secondsAfterEpoch / 30);
    }

    public TotpResponse generateTotp(WasteBin wasteBin, long timeInSeconds) throws CodeGenerationException {
        return new TotpResponse(
                generateTotp(
                        wasteBin.getSecretBase32(),
                        timeInSeconds
                ), wasteBin.getId()
        );
    }
}
