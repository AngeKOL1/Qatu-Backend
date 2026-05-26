package com.example.Qatu.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${qatu.firebase.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {

        // Si no hay path configurado, Firebase queda desactivado
        if (credentialsPath == null || credentialsPath.isBlank()) {
            log.warn("Firebase no configurado — FCM desactivado");
            return;  // ← sale aquí, no intenta leer ningún archivo
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource(credentialsPath);

                if (!resource.exists()) {
                    log.warn("Archivo Firebase no encontrado: {} — FCM desactivado",
                        credentialsPath);
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials
                        .fromStream(resource.getInputStream()))
                    .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase inicializado correctamente");
            }
        } catch (IOException e) {
            log.error("Error inicializando Firebase: {}", e.getMessage());
        }
    }
}