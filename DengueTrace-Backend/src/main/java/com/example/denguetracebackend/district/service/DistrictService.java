package com.example.denguetracebackend.district.service;

import com.example.denguetracebackend.common.exception.DuplicateResourceException;
import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.dto.DistrictRequestDTO;
import com.example.denguetracebackend.district.dto.DistrictResponseDTO;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;

    public List<DistrictResponseDTO> getAll() {
        return districtRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DistrictResponseDTO getById(Long id) {
        return toResponse(findDistrict(id));
    }

    @Transactional
    public DistrictResponseDTO create(DistrictRequestDTO request) {
        if (districtRepository.existsByNameIgnoreCaseAndDepartmentIgnoreCase(request.name(), request.department())) {
            throw new DuplicateResourceException("District " + request.name() + " already exists in " + request.department());
        }
        District district = District.builder()
                .name(request.name())
                .department(request.department())
                .province(request.province())
                .population(request.population())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        return toResponse(districtRepository.save(district));
    }

    @Transactional
    public DistrictResponseDTO update(Long id, DistrictRequestDTO request) {
        District district = findDistrict(id);
        district.setName(request.name());
        district.setDepartment(request.department());
        district.setProvince(request.province());
        district.setPopulation(request.population());
        district.setLatitude(request.latitude());
        district.setLongitude(request.longitude());
        return toResponse(districtRepository.save(district));
    }

    @Transactional
    public void delete(Long id) {
        District district = findDistrict(id);
        districtRepository.delete(district);
    }

    private District findDistrict(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("District", id));
    }

    private DistrictResponseDTO toResponse(District d) {
        return new DistrictResponseDTO(d.getId(), d.getName(), d.getDepartment(), d.getProvince(),
                d.getPopulation(), d.getLatitude(), d.getLongitude());
    }
}
