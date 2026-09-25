package com.example.denguetracebackend.climatedata.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClimateDataRequestDTO(
        @NotNull Long districtId,
        @NotNull Double temperature,
        @NotNull Double humidity,
        @NotNull Double precipitationMm,
        @NotNull LocalDate recordedDate
) {}
