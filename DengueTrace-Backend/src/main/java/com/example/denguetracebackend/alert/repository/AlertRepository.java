package com.example.denguetracebackend.alert.repository;

import com.example.denguetracebackend.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByDistrictIdAndActiveTrue(Long districtId);
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();
}
