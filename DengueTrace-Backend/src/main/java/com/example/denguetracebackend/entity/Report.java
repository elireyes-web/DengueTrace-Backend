package com.example.denguetracebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El DNI del reportante es obligatorio")
    @Column(name = "reporter_dni", nullable = false, length = 8)
    private String reporterDni;

    @NotBlank(message = "El distrito es obligatorio")
    @Column(nullable = false)
    private String district;

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

