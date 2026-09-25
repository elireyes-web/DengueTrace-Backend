package com.example.denguetracebackend.alert.controller;

import com.example.denguetracebackend.alert.dto.AlertCreateRequestDTO;
import com.example.denguetracebackend.alert.dto.AlertResponseDTO;
import com.example.denguetracebackend.alert.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getActive() {
        return ResponseEntity.ok(alertService.getActive());
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<AlertResponseDTO>> getByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(alertService.getByDistrict(districtId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertResponseDTO> create(@Valid @RequestBody AlertCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.create(request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        alertService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
