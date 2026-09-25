package com.example.denguetracebackend.news.dto;

import java.time.LocalDateTime;

public record NewsResponseDTO(
        Long id,
        Long districtId,
        String title,
        String summary,
        String source,
        String url,
        LocalDateTime publishedAt
) {}
