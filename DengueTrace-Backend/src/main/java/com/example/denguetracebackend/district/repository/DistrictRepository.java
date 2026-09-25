package com.example.denguetracebackend.district.repository;

import com.example.denguetracebackend.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);
    boolean existsByNameIgnoreCaseAndDepartmentIgnoreCase(String name, String department);
}
