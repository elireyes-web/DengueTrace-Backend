package com.example.denguetracebackend.user.dto;

import com.example.denguetracebackend.common.enums.NotificationChannel;
import jakarta.validation.constraints.Min;

public record UserUpdateRequestDTO(
        String fullName,
        String phone,
        Long districtId,
        NotificationChannel preferredChannel,
        @Min(1) Integer alertRadiusKm
) {}
