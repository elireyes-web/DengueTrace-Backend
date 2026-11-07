package com.example.denguetracebackend.district.repository;

import com.example.denguetracebackend.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Page<District> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<District> findByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);
    boolean existsByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);
    }
