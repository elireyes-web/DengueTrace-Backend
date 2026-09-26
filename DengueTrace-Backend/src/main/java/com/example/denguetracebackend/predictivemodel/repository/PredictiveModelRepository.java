package com.example.denguetracebackend.predictivemodel.repository;

import com.example.denguetracebackend.predictivemodel.entity.PredictiveModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictiveModelRepository
        extends JpaRepository<PredictiveModel, Long> {

    Optional<PredictiveModel>
    findTopByDistrictIdOrderByGeneratedAtDesc(Long districtId);

    List<PredictiveModel>
    findByDistrictIdOrderByGeneratedAtDesc(Long districtId);
}