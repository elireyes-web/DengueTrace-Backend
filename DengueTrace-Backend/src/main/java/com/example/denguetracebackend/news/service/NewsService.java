package com.example.denguetracebackend.news.service;

import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.common.integration.gdelt.GdeltNewsService;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.news.dto.NewsRequestDTO;
import com.example.denguetracebackend.news.dto.NewsResponseDTO;
import com.example.denguetracebackend.news.entity.News;
import com.example.denguetracebackend.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final DistrictRepository districtRepository;
    private final GdeltNewsService gdeltNewsService;

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

    /**
     * Previously "external news via GDELT" was only promised in the proposal but never
     * actually called: news could only be entered by hand through POST /api/v1/news.
     * This pulls real dengue-related articles from the GDELT Doc API for the district
     * and stores the ones we don't already have (deduplicated by URL).
     */
    @Async("taskExecutor")
    @Transactional
    public void syncFromGdelt(Long districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> ResourceNotFoundException.of("District", districtId));

        List<GdeltNewsService.ExternalNewsItem> items = gdeltNewsService.searchDengueNews(district.getName());
        int saved = 0;
        for (var item : items) {
            if (item.url() == null || item.url().isBlank() || newsRepository.existsByUrl(item.url())) {
                continue;
            }
            News news = News.builder()
                    .district(district)
                    .title(item.title())
                    .summary(null)
                    .source(item.source())
                    .url(item.url())
                    .publishedAt(item.publishedAt())
                    .build();
            newsRepository.save(news);
            saved++;
        }
        log.info("GDELT sync for district {}: {} new articles saved ({} fetched)", district.getName(), saved, items.size());
    }

    private NewsResponseDTO toResponse(News n) {
        return new NewsResponseDTO(n.getId(), n.getDistrict().getId(), n.getTitle(), n.getSummary(),
                n.getSource(), n.getUrl(), n.getPublishedAt());
    }
}
