package com.example.denguetracebackend.report.service;

import com.example.denguetracebackend.report.entity.Report;
import com.example.denguetracebackend.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    public List<Report> getReportsByDistrict(String district) {
        return reportRepository.findByDistrict(district);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
