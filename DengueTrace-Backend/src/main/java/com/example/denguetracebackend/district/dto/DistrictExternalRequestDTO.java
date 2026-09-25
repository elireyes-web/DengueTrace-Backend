package com.example.denguetracebackend.district.dto;


import jakarta.validation.constraints.Positive;

public class DistrictExternalRequestDTO {
    double longitud;
    double latitude;
    @Positive(message = "Population must be zero or positive") Long population; //cambio a positivo
}
