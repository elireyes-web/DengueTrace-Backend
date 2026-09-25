package com.example.denguetracebackend.news.service;

import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.news.dto.NewsRequestDTO;
import com.example.denguetracebackend.news.dto.NewsResponseDTO;
import com.example.denguetracebackend.news.entity.News;
import com.example.denguetracebackend.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public NewsResponseDTO create(NewsRequestDTO request) {
        District district = districtRepository.findById(request.districtId())
                .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));
        News news = News.builder()
                .district(district)
                .title(request.title())
                .summary(request.summary())
                .source(request.source())
                .url(request.url())
                .publishedAt(request.publishedAt())
                .build();
        return toResponse(newsRepository.save(news));
    }

    public List<NewsResponseDTO> getByDistrict(Long districtId) {
        return newsRepository.findByDistrictIdOrderByPublishedAtDesc(districtId).stream().map(this::toResponse).toList();
    }

    private NewsResponseDTO toResponse(News n) {
        return new NewsResponseDTO(n.getId(), n.getDistrict().getId(), n.getTitle(), n.getSummary(),
                n.getSource(), n.getUrl(), n.getPublishedAt());
    }
}
