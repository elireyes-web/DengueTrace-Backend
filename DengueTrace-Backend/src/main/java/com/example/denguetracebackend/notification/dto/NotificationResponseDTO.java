package com.example.denguetracebackend.notification.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NotificationResponseDTO {

    private Long notificationId;
    private Long userId;
    private Long alertId;
    private String channel;
    private String cause;
    private Timestamp fechaEnvio;
}