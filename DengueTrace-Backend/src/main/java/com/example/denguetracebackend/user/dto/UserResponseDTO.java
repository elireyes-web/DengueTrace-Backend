package com.example.denguetracebackend.user.dto;

import com.example.denguetracebackend.common.enums.NotificationChannel;
import com.example.denguetracebackend.common.enums.Role;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        String phone,
        Long districtId,
        String districtName,
        Role role,
        NotificationChannel preferredChannel,
        Integer alertRadiusKm
) {}
