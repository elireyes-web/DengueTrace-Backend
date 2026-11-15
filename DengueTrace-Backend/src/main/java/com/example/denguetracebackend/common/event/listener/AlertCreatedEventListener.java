package com.example.denguetracebackend.common.event.listener;

import com.example.denguetracebackend.alert.repository.AlertRepository;
import com.example.denguetracebackend.common.email.EmailService;
import com.example.denguetracebackend.common.enums.NotificationChannel;
import com.example.denguetracebackend.common.enums.NotificationStatus;
import com.example.denguetracebackend.common.event.AlertCreatedEvent;
import com.example.denguetracebackend.common.integration.push.FcmPushService;
import com.example.denguetracebackend.common.integration.sms.SmsService;
import com.example.denguetracebackend.notification.entity.Notification;
import com.example.denguetracebackend.notification.repository.NotificationRepository;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertCreatedEventListener {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FcmPushService fcmPushService;
    private final SmsService smsService;
    private final EmailService emailService;

    // An asynchronous listener runs only after the alert was committed.
    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAlertCreated(AlertCreatedEvent event) {
        var alert = alertRepository.findById(event.getAlertId()).orElse(null);
        if (alert == null) {
            log.warn("Alert {} was not found; skipping notifications", event.getAlertId());
            return;
        }

        List<User> affectedUsers = userRepository.findByDistrictId(alert.getDistrict().getId());
        String title = "Alerta de riesgo de Dengue - " + alert.getDistrict().getName();
        String body = alert.getMessage();

        int sent = 0;
        for (User user : affectedUsers) {
            NotificationChannel channel = user.getPreferredChannel() != null
                    ? user.getPreferredChannel()
                    : NotificationChannel.PUSH;

            boolean delivered = dispatch(channel, user, title, body, alert.getDistrict().getName());

            Notification notification = Notification.builder()
                    .alert(alert)
                    .user(user)
                    .channel(channel)
                    .status(delivered ? NotificationStatus.SENT : NotificationStatus.FAILED)
                    .sentAt(delivered ? LocalDateTime.now() : null)
                    .build();
            notificationRepository.save(notification);

            if (delivered) sent++;
        }
        log.info("Dispatched alert {} to {} users ({} delivered, {} failed)",
                alert.getId(), affectedUsers.size(), sent, affectedUsers.size() - sent);
    }

    private boolean dispatch(NotificationChannel channel, User user, String title, String body, String districtName) {
        try {
            return switch (channel) {
                case PUSH -> fcmPushService.sendPush(user.getFcmToken(), title, body);
                case SMS -> smsService.sendSms(user.getPhone(), title + ": " + body);
                case EMAIL -> emailService.sendAlertEmail(user.getEmail(), districtName, body);
            };
        } catch (RuntimeException ex) {
            log.error("Alert dispatch failed for user {} via {}: {}", user.getId(), channel, ex.getMessage());
            return false;
        }
    }
}
