package com.example.denguetracebackend.district.entity;

import com.example.denguetracebackend.climatedata.entity.ClimateData;
import com.example.denguetracebackend.historicalcase.entity.HistoricalCase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "districts", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "department"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String department;

    @Column(length = 100)
    private String province;

    @PositiveOrZero
    private Long population;

    private Double latitude;

    private Double longitude;

    @Builder.Default
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalCase> historicalCases = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClimateData> climateData = new ArrayList<>();
}
