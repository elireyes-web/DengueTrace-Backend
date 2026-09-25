package com.example.denguetracebackend.climatedata.entity;

import com.example.denguetracebackend.district.entity.District;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "climate_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClimateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private Double precipitationMm;

    @Column(nullable = false)
    private LocalDate recordedDate;
}
