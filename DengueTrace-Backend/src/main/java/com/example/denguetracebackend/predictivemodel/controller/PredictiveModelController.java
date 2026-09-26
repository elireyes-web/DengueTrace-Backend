package com.example.denguetracebackend.predictivemodel.controller;

import com.example.denguetracebackend.predictivemodel.dto.PredictiveModelResponseDTO;
import com.example.denguetracebackend.predictivemodel.service.PredictiveModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/predictive-models")
@RequiredArgsConstructor
public class PredictiveModelController {

    private final PredictiveModelService predictiveModelService;

    @PostMapping("/district/{districtId}/generate")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MODERATOR')"
    )
    public ResponseEntity<PredictiveModelResponseDTO>
    generate(
            @PathVariable Long districtId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        predictiveModelService.generate(
                                districtId
                        )
                );
    }

    @GetMapping("/district/{districtId}/latest")
    public ResponseEntity<PredictiveModelResponseDTO>
    getLatest(
            @PathVariable Long districtId
    ) {

        return ResponseEntity.ok(
                predictiveModelService
                        .getLatestByDistrict(
                                districtId
                        )
        );
    }

    @GetMapping("/district/{districtId}/history")
    public ResponseEntity<
            List<PredictiveModelResponseDTO>
            >
    getHistory(
            @PathVariable Long districtId
    ) {

        return ResponseEntity.ok(
                predictiveModelService
                        .getHistoryByDistrict(
                                districtId
                        )
        );
    }
}