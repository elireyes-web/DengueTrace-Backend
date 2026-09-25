package com.example.denguetracebackend.common.event.listener;

import com.example.denguetracebackend.common.email.EmailService;
import com.example.denguetracebackend.common.enums.NotificationChannel;
import com.example.denguetracebackend.common.enums.NotificationStatus;
import com.example.denguetracebackend.common.event.AlertCreatedEvent;
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
 * in the affected district: creates a Notification record and dispatches it
 * through the user's preferred channel (email stands in for push/SMS delivery).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertCreatedEventListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Async("taskExecutor")
    @EventListener
    @Transactional
    public void onAlertCreated(AlertCreatedEvent event) {
        var alert = event.getAlert();
        List<User> affectedUsers = userRepository.findByDistrictId(alert.getDistrict().getId());

        for (User user : affectedUsers) {
            Notification notification = Notification.builder()
                    .alert(alert)
                    .user(user)
                    .channel(user.getPreferredChannel() != null ? user.getPreferredChannel() : NotificationChannel.PUSH)
                    .status(NotificationStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
            emailService.sendAlertEmail(user.getEmail(), alert.getDistrict().getName(), alert.getMessage());
        }
        log.info("Dispatched {} notifications for alert {}", affectedUsers.size(), alert.getId());
    }
}
