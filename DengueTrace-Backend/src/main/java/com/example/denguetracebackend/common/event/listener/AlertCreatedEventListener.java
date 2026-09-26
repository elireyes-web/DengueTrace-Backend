package com.example.denguetracebackend.common.event.listener;

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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Listens for AlertCreatedEvent and, asynchronously, notifies every user registered
 * in the affected district through their ACTUAL preferred channel (PUSH via Firebase
 * or SMS via Twilio), persisting the real delivery outcome.
 *
 * Fix vs. previous version:
 *  - previously this always sent an email and ignored user.preferredChannel entirely.
 *  - previously it always saved status = SENT, even when nothing was actually sent.
 * Now it dispatches per-channel and stores SENT only when the provider confirms success,
 * FAILED otherwise (e.g. missing token/phone, provider error).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertCreatedEventListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FcmPushService fcmPushService;
    private final SmsService smsService;

    @Async("taskExecutor")
    @EventListener
    @Transactional
    public void onAlertCreated(AlertCreatedEvent event) {
        var alert = event.getAlert();
        List<User> affectedUsers = userRepository.findByDistrictId(alert.getDistrict().getId());

        String title = "Alerta de riesgo de Dengue - " + alert.getDistrict().getName();
        String body = alert.getMessage();

        int sent = 0;
        for (User user : affectedUsers) {
            NotificationChannel channel = user.getPreferredChannel() != null
                    ? user.getPreferredChannel()
                    : NotificationChannel.PUSH;

            boolean delivered = switch (channel) {
                case PUSH -> fcmPushService.sendPush(user.getFcmToken(), title, body);
                case SMS -> smsService.sendSms(user.getPhone(), title + ": " + body);
            };

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
}
