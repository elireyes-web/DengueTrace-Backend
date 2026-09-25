package com.example.denguetracebackend.historicalcase.dto;

import java.time.LocalDate;

public record HistoricalCaseResponseDTO(
        Long id,
        Long districtId,
        String districtName,
        Integer caseCount,
        LocalDate weekStartDate,
        Integer year
) {}
