package com.example.denguetracebackend.user.service;

import com.example.denguetracebackend.user.dto.UserResponseDTO;
import com.example.denguetracebackend.user.entity.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getDistrict() != null ? user.getDistrict().getId() : null,
                user.getDistrict() != null ? user.getDistrict().getName() : null,
                user.getRole(),
                user.getPreferredChannel(),
                user.getAlertRadiusKm(),
                user.getFcmToken() != null && !user.getFcmToken().isBlank()
        );
    }
}
