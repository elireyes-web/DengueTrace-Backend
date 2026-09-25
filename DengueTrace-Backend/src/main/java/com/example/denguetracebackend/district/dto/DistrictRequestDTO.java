package com.example.denguetracebackend.district.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record DistrictRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Department is required") String department,
        String province,
        @PositiveOrZero(message = "Population must be zero or positive") Long population,
        Double latitude,
        Double longitude
) {}
