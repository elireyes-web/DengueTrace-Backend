package com.example.denguetracebackend.climatedata.controller;

import com.example.denguetracebackend.climatedata.dto.ClimateDataRequestDTO;
import com.example.denguetracebackend.climatedata.dto.ClimateDataResponseDTO;
import com.example.denguetracebackend.climatedata.service.ClimateDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/climate-data")
@RequiredArgsConstructor
public class ClimateDataController {

    private final ClimateDataService climateDataService;

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<ClimateDataResponseDTO>> getByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(climateDataService.getByDistrict(districtId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClimateDataResponseDTO> create(@Valid @RequestBody ClimateDataRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(climateDataService.create(request));
    }
}
