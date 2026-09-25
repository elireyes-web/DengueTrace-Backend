package com.example.denguetracebackend.user.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponseDTO user
) {
    public AuthResponseDTO(String accessToken, String refreshToken, UserResponseDTO user) {
        this(accessToken, refreshToken, "Bearer", user);
    }
}
