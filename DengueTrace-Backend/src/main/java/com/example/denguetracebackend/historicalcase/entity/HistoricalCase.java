package com.example.denguetracebackend.historicalcase.entity;

import com.example.denguetracebackend.district.entity.District;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "historical_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer caseCount;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(nullable = false)
    private Integer year;
}
