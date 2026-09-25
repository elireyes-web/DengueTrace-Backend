package com.example.denguetracebackend.climatedata.service;

import com.example.denguetracebackend.climatedata.dto.ClimateDataRequestDTO;
import com.example.denguetracebackend.climatedata.dto.ClimateDataResponseDTO;
import com.example.denguetracebackend.climatedata.entity.ClimateData;
import com.example.denguetracebackend.climatedata.repository.ClimateDataRepository;
import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClimateDataService {

    private final ClimateDataRepository climateDataRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public ClimateDataResponseDTO create(ClimateDataRequestDTO request) {
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));
        ClimateData entity = ClimateData.builder()
                .district(district)
                .temperature(request.temperature())
                .humidity(request.humidity())
                .precipitationMm(request.precipitationMm())
                .recordedDate(request.recordedDate())
                .build();
        return toResponse(climateDataRepository.save(entity));
    }

    public List<ClimateDataResponseDTO> getByDistrict(Long districtId) {
        return climateDataRepository.findByDistrictIdOrderByRecordedDateAsc(districtId).stream().map(this::toResponse).toList();
    }

    private ClimateDataResponseDTO toResponse(ClimateData c) {
        return new ClimateDataResponseDTO(c.getId(), c.getDistrict().getId(), c.getTemperature(),
                c.getHumidity(), c.getPrecipitationMm(), c.getRecordedDate());
    }
}
