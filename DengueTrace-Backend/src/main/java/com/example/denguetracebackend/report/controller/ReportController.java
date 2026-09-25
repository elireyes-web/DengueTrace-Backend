package com.example.denguetracebackend.report.controller;

import com.example.denguetracebackend.report.entity.Report;
import com.example.denguetracebackend.report.service.ReportService;
import com.example.denguetracebackend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report, Authentication auth) {
        Long usuarioId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        report.setUsuarioId(usuarioId);
        Report saved = reportService.createReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<Report>> getReportsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(reportService.getReportsByDistrict(district));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
