package com.example.denguetracebackend.report.service;

import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.report.dto.ReportRequestDTO;
import com.example.denguetracebackend.report.dto.ReportResponseDTO;
import com.example.denguetracebackend.report.entity.Report;
import com.example.denguetracebackend.report.repository.ReportRepository;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public ReportResponseDTO createReport(
            ReportRequestDTO request,
            String authenticatedEmail
    ) {

        User user = userRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + authenticatedEmail
                        )
                );

        District district = districtRepository
                .findById(request.districtId())
                .orElseThrow(() ->
                        ResourceNotFoundException.of(
                                "District",
                                request.districtId()
                        )
                );

        Report report = Report.builder()
                .user(user)
                .district(district)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .symptoms(request.symptoms())
                .build();

        Report savedReport = reportRepository.save(report);

        return toResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getAllReports() {

        return reportRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponseDTO getReportById(Long id) {

        return toResponse(
                findReport(id)
        );
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getReportsByDistrict(
            String districtName
    ) {

        return reportRepository
                .findByDistrict_NameIgnoreCase(districtName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getMyReports(
            String authenticatedEmail
    ) {

        User user = userRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + authenticatedEmail
                        )
                );

        return reportRepository
                .findByUser_Id(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteReport(Long id) {

        Report report = findReport(id);

        reportRepository.delete(report);
    }

    private Report findReport(Long id) {

        return reportRepository
                .findById(id)
                .orElseThrow(() ->
                        ResourceNotFoundException.of(
                                "Report",
                                id
                        )
                );
    }

    private ReportResponseDTO toResponse(
            Report report
    ) {

        return new ReportResponseDTO(
                report.getId(),
                report.getDistrict().getId(),
                report.getDistrict().getName(),
                report.getLatitude(),
                report.getLongitude(),
                report.getSymptoms(),
                report.getCreatedAt()
        );
    }
}