package com.example.denguetracebackend.news.repository;

import com.example.denguetracebackend.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByDistrictIdOrderByPublishedAtDesc(Long districtId);

    boolean existsByUrl(String url);
}
