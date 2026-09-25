package com.example.denguetracebackend.historicalcase.service;

import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.historicalcase.dto.HistoricalCaseRequestDTO;
import com.example.denguetracebackend.historicalcase.dto.HistoricalCaseResponseDTO;
import com.example.denguetracebackend.historicalcase.entity.HistoricalCase;
import com.example.denguetracebackend.historicalcase.repository.HistoricalCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalCaseService {

    private final HistoricalCaseRepository historicalCaseRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public HistoricalCaseResponseDTO create(HistoricalCaseRequestDTO request) {
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));
        HistoricalCase entity = HistoricalCase.builder()
                .district(district)
                .caseCount(request.caseCount())
                .weekStartDate(request.weekStartDate())
                .year(request.year())
                .build();
        return toResponse(historicalCaseRepository.save(entity));
    }

    public List<HistoricalCaseResponseDTO> getByDistrict(Long districtId) {
        return historicalCaseRepository.findByDistrictIdOrderByWeekStartDateAsc(districtId).stream().map(this::toResponse).toList();
    }

    private HistoricalCaseResponseDTO toResponse(HistoricalCase h) {
        return new HistoricalCaseResponseDTO(h.getId(), h.getDistrict().getId(), h.getDistrict().getName(),
                h.getCaseCount(), h.getWeekStartDate(), h.getYear());
    }
}
