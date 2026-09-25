package com.example.denguetracebackend.district.dto;

public record DistrictResponseDTO(
        Long id,
        String name,
        String department,
        String province,
        Long population,
        Double latitude,
        Double longitude
) {}
