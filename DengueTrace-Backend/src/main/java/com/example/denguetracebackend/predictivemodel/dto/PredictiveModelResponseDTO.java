package com.example.denguetracebackend.predictivemodel.dto;

import com.example.denguetracebackend.common.enums.RiskLevel;

import java.time.LocalDateTime;

public record PredictiveModelResponseDTO(

        Long id,

        Long districtId,

        String districtName,

        Double historicalAverage,

        Double recentAverage,

        Double trendFactor,

        Double climateFactor,

        Integer projectedWeek1,

        Integer projectedWeek2,

        Integer projectedWeek3,

        Integer projectedWeek4,

        RiskLevel riskLevel,

        Double confidenceLevel,

        LocalDateTime generatedAt

) {}