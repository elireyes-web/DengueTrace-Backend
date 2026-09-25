package com.example.denguetracebackend.historicalcase.repository;

import com.example.denguetracebackend.historicalcase.entity.HistoricalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricalCaseRepository extends JpaRepository<HistoricalCase, Long> {
    List<HistoricalCase> findByDistrictIdOrderByWeekStartDateAsc(Long districtId);
}
