package com.example.denguetracebackend.common.integration.push;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Sends push notifications through Firebase Cloud Messaging.
 * Returns a plain boolean so callers (AlertCreatedEventListener) can persist
 * a real SENT/FAILED status instead of assuming success.
 */
@Service
@Slf4j
public class FcmPushService {

    public boolean sendPush(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("Cannot send push: user has no fcmToken registered.");
            return false;
        }
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase is not initialized (missing FIREBASE_CREDENTIALS_BASE64); push not sent.");
            return false;
        }

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM push sent, message id={}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("FCM push failed: {}", e.getMessage());
            return false;
        }
    }
}
