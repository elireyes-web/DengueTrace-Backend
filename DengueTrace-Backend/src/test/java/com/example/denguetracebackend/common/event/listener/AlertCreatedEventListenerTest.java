package com.example.denguetracebackend.common.event.listener;

import com.example.denguetracebackend.alert.entity.Alert;
import com.example.denguetracebackend.alert.repository.AlertRepository;
import com.example.denguetracebackend.common.email.EmailService;
import com.example.denguetracebackend.common.enums.NotificationChannel;
import com.example.denguetracebackend.common.enums.NotificationStatus;
import com.example.denguetracebackend.common.enums.RiskLevel;
import com.example.denguetracebackend.common.event.AlertCreatedEvent;
import com.example.denguetracebackend.common.integration.push.FcmPushService;
import com.example.denguetracebackend.common.integration.sms.SmsService;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.notification.entity.Notification;
import com.example.denguetracebackend.notification.repository.NotificationRepository;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertCreatedEventListenerTest {

    @Mock AlertRepository alertRepository;
    @Mock UserRepository userRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock FcmPushService fcmPushService;
    @Mock SmsService smsService;
    @Mock EmailService emailService;
    @InjectMocks AlertCreatedEventListener listener;

    private Alert arrange(NotificationChannel channel) {
        District district = District.builder().id(42L).name("Barranco").build();
        Alert alert = Alert.builder().id(7L).district(district)
                .message("Alerta de prueba").riskLevel(RiskLevel.HIGH).build();
        User user = User.builder().id(2L).email("prueba@example.com")
                .preferredChannel(channel).district(district).build();
        when(alertRepository.findById(7L)).thenReturn(Optional.of(alert));
        when(userRepository.findByDistrictId(42L)).thenReturn(List.of(user));
        return alert;
    }

    @Test
    void sendsEmailOnlyWhenEmailIsSelected() {
        Alert alert = arrange(NotificationChannel.EMAIL);
        when(emailService.sendAlertEmail("prueba@example.com", "Barranco", "Alerta de prueba"))
                .thenReturn(true);

        listener.onAlertCreated(new AlertCreatedEvent(this, alert.getId()));

        verifyNoInteractions(fcmPushService, smsService);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(NotificationChannel.EMAIL, captor.getValue().getChannel());
        assertEquals(NotificationStatus.SENT, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getSentAt());
    }

    @Test
    void failedEmailIsNotMarkedSent() {
        Alert alert = arrange(NotificationChannel.EMAIL);
        when(emailService.sendAlertEmail(anyString(), anyString(), anyString())).thenReturn(false);

        listener.onAlertCreated(new AlertCreatedEvent(this, alert.getId()));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals(NotificationStatus.FAILED, captor.getValue().getStatus());
        assertNull(captor.getValue().getSentAt());
    }

    @Test
    void pushPreferenceDoesNotSendEmail() {
        Alert alert = arrange(NotificationChannel.PUSH);
        when(fcmPushService.sendPush(isNull(), anyString(), anyString())).thenReturn(false);

        listener.onAlertCreated(new AlertCreatedEvent(this, alert.getId()));

        verifyNoInteractions(emailService, smsService);
        verify(notificationRepository).save(any(Notification.class));
    }
}
