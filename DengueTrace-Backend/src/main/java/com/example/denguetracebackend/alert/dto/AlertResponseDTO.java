package com.example.denguetracebackend.alert.dto;

import com.example.denguetracebackend.common.enums.RiskLevel;

import java.time.LocalDateTime;

public record AlertResponseDTO(
        Long id,
        Long districtId,
        String districtName,
        RiskLevel riskLevel,
        String message,
        Double thresholdExceeded,
        boolean active,
        LocalDateTime createdAt
) {}
