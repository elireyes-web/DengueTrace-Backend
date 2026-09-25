package com.example.denguetracebackend.alert.service;

import com.example.denguetracebackend.alert.dto.AlertCreateRequestDTO;
import com.example.denguetracebackend.alert.dto.AlertResponseDTO;
import com.example.denguetracebackend.alert.entity.Alert;
import com.example.denguetracebackend.alert.repository.AlertRepository;
import com.example.denguetracebackend.common.event.AlertCreatedEvent;
import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final DistrictRepository districtRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AlertResponseDTO create(AlertCreateRequestDTO request) {
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));

        Alert alert = Alert.builder()
                .district(district)
                .riskLevel(request.riskLevel())
                .message(request.message())
                .thresholdExceeded(request.thresholdExceeded())
                .active(true)
                .build();

        Alert saved = alertRepository.save(alert);
        // Triggers async notification dispatch (push/SMS/email) to every user registered in the district.
        eventPublisher.publishEvent(new AlertCreatedEvent(this, saved));

        return toResponse(saved);
    }

    public List<AlertResponseDTO> getActive() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<AlertResponseDTO> getByDistrict(Long districtId) {
        return alertRepository.findByDistrictIdAndActiveTrue(districtId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deactivate(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Alert", id));
        alert.setActive(false);
        alertRepository.save(alert);
    }

    private AlertResponseDTO toResponse(Alert a) {
        return new AlertResponseDTO(a.getId(), a.getDistrict().getId(), a.getDistrict().getName(),
                a.getRiskLevel(), a.getMessage(), a.getThresholdExceeded(), a.isActive(), a.getCreatedAt());
    }
}
