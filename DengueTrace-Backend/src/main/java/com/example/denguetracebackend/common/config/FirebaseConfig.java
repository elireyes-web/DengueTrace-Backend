package com.example.denguetracebackend.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Initializes Firebase Admin SDK for push notifications (FCM).
 *
 * The service account JSON is NEVER stored in the repo. It is passed as a
 * Base64-encoded environment variable (FIREBASE_CREDENTIALS_BASE64).
 * See PATCH_NOTES.md for how to generate and set it.
 *
 * If the variable is missing (e.g. local dev without Firebase set up), this
 * silently skips initialization instead of crashing the whole application,
 * so the rest of the backend keeps working and push simply won't be sent.
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${app.external.firebase.credentials-base64:}")
    private String credentialsBase64;

    @PostConstruct
    public void init() {
        if (credentialsBase64 == null || credentialsBase64.isBlank()) {
            log.warn("FIREBASE_CREDENTIALS_BASE64 not set; push notifications are disabled.");
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(credentialsBase64.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize Firebase Admin SDK: {}", e.getMessage());
        }
    }
}
