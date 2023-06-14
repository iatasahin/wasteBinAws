package com.example.waste.config;

import com.example.waste.controller.InstantHolder;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Instant;

@Configuration
public class TotpConfig {
    @Bean
    CodeGenerator codeGenerator (){
        return new DefaultCodeGenerator();
    }

    @Bean
    SecretGenerator secretGenerator(){
        return new DefaultSecretGenerator();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    InstantHolder mcuEpoch() {
        return new InstantHolder().setInstant(Instant.now());
    }
}
