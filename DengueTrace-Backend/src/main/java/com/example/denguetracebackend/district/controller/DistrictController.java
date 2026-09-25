package com.example.denguetracebackend.district.controller;

import com.example.denguetracebackend.district.dto.DistrictRequestDTO;
import com.example.denguetracebackend.district.dto.DistrictResponseDTO;
import com.example.denguetracebackend.district.service.DistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    public ResponseEntity<List<DistrictResponseDTO>> getAll() {
        return ResponseEntity.ok(districtService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(districtService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictResponseDTO> create(@Valid @RequestBody DistrictRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(districtService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DistrictRequestDTO request) {
        return ResponseEntity.ok(districtService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        districtService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
