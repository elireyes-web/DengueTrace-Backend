package com.example.denguetracebackend.report.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportRequestDTO(

        Long districtId,

        @DecimalMin(
                value = "-90.0",
                message = "Latitude must be greater than or equal to -90"
        )
        @DecimalMax(
                value = "90.0",
                message = "Latitude must be less than or equal to 90"
        )
        Double latitude,

        @DecimalMin(
                value = "-180.0",
                message = "Longitude must be greater than or equal to -180"
        )
        @DecimalMax(
                value = "180.0",
                message = "Longitude must be less than or equal to 180"
        )
        Double longitude,

        @NotBlank(message = "Symptoms are required")
        @Size(
                max = 2000,
                message = "Symptoms must not exceed 2000 characters"
        )
        String symptoms

) {}