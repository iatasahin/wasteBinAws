package com.example.waste.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FirestoreConfig {
    @Bean
    public Firestore firestore(){
        FirebaseOptions options;
        try{
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }
}
