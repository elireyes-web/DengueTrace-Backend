package com.example.denguetracebackend.climatedata.repository;

import com.example.denguetracebackend.climatedata.entity.ClimateData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClimateDataRepository extends JpaRepository<ClimateData, Long> {
    List<ClimateData> findByDistrictIdOrderByRecordedDateAsc(Long districtId);
}
