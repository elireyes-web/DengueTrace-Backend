package com.example.denguetracebackend.alert.dto;

import com.example.denguetracebackend.common.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertCreateRequestDTO(
        @NotNull Long districtId,
        @NotNull RiskLevel riskLevel,
        @NotBlank String message,
        Double thresholdExceeded
) {}
