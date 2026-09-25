package com.example.denguetracebackend.historicalcase.controller;

import com.example.denguetracebackend.historicalcase.dto.HistoricalCaseRequestDTO;
import com.example.denguetracebackend.historicalcase.dto.HistoricalCaseResponseDTO;
import com.example.denguetracebackend.historicalcase.service.HistoricalCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historical-cases")
@RequiredArgsConstructor
public class HistoricalCaseController {

    private final HistoricalCaseService historicalCaseService;

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<HistoricalCaseResponseDTO>> getByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(historicalCaseService.getByDistrict(districtId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HistoricalCaseResponseDTO> create(@Valid @RequestBody HistoricalCaseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historicalCaseService.create(request));
    }
}
