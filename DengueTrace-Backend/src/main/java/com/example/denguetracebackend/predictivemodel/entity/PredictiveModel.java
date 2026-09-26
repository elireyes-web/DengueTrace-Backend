package com.example.denguetracebackend.predictivemodel.entity;

import com.example.denguetracebackend.common.enums.RiskLevel;
import com.example.denguetracebackend.district.entity.District;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "predictive_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictiveModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private Double historicalAverage;

    @Column(nullable = false)
    private Double recentAverage;

    @Column(nullable = false)
    private Double trendFactor;

    @Column(nullable = false)
    private Double climateFactor;

    @Column(nullable = false)
    private Integer projectedWeek1;

    @Column(nullable = false)
    private Integer projectedWeek2;

    @Column(nullable = false)
    private Integer projectedWeek3;

    @Column(nullable = false)
    private Integer projectedWeek4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private Double confidenceLevel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}