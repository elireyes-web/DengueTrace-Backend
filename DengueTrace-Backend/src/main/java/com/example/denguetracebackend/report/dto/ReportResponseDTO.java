package com.example.denguetracebackend.report.dto;

import java.time.LocalDateTime;

public record ReportResponseDTO(

        Long id,

        Long districtId,

        String districtName,

        Double latitude,

        Double longitude,

        String symptoms,

        LocalDateTime createdAt

) {}