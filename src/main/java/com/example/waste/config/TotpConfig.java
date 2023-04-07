package com.example.waste.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
