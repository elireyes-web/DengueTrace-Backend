package com.example.denguetracebackend.report.controller;

import com.example.denguetracebackend.report.dto.ReportRequestDTO;
import com.example.denguetracebackend.report.dto.ReportResponseDTO;
import com.example.denguetracebackend.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(
            @Valid @RequestBody ReportRequestDTO request,
            Authentication authentication
    ) {

        ReportResponseDTO created =
                reportService.createReport(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MODERATOR')"
    )
    public ResponseEntity<List<ReportResponseDTO>>
    getAllReports() {

        return ResponseEntity.ok(
                reportService.getAllReports()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReportResponseDTO>>
    getMyReports(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                reportService.getMyReports(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO>
    getReportById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                reportService.getReportById(id)
        );
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<ReportResponseDTO>>
    getReportsByDistrict(
            @PathVariable String district
    ) {

        return ResponseEntity.ok(
                reportService.getReportsByDistrict(
                        district
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MODERATOR')"
    )
    public ResponseEntity<Void>
    deleteReport(
            @PathVariable Long id
    ) {

        reportService.deleteReport(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}