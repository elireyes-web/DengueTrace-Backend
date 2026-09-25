package com.example.denguetracebackend.historicalcase.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record HistoricalCaseRequestDTO(
        @NotNull Long districtId,
        @PositiveOrZero Integer caseCount,
        @NotNull LocalDate weekStartDate,
        @NotNull Integer year
) {}
