package com.example.denguetracebackend.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NewsRequestDTO(
        @NotNull Long districtId,
        @NotBlank String title,
        String summary,
        String source,
        String url,
        @NotNull LocalDateTime publishedAt
) {}
