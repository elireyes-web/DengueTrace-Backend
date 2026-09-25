package com.example.denguetracebackend.district.dto;

import com.example.denguetracebackend.common.enums.RiskLevel;

public record DistrictRiskSummaryDTO(
        Long districtId,
        String districtName,
        RiskLevel riskLevel,
        Integer projectedCasesNextWeek,
        Double confidenceLevel
) {}
