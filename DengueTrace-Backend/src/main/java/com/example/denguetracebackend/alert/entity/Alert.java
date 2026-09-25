package com.example.denguetracebackend.alert.entity;

import com.example.denguetracebackend.common.enums.RiskLevel;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.news.entity.News;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false, length = 500)
    private String message;

    private Double thresholdExceeded;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "alert_news",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "news_id"))
    private Set<News> relatedNews = new HashSet<>();
}
