package com.example.denguetracebackend.report.repository;

import com.example.denguetracebackend.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByDistrict(String district);
    List<Report> findByUsuarioId(Long usuarioId);
}
