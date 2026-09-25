package com.example.denguetracebackend.climatedata.dto;

import java.time.LocalDate;

public record ClimateDataResponseDTO(
        Long id,
        Long districtId,
        Double temperature,
        Double humidity,
        Double precipitationMm,
        LocalDate recordedDate
) {}
