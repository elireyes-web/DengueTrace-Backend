package com.example.denguetracebackend.news.controller;

import com.example.denguetracebackend.news.dto.NewsRequestDTO;
import com.example.denguetracebackend.news.dto.NewsResponseDTO;
import com.example.denguetracebackend.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<NewsResponseDTO>> getByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(newsService.getByDistrict(districtId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsResponseDTO> create(@Valid @RequestBody NewsRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(request));
    }
}
