package com.example.denguetracebackend.common.integration.gdelt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the GDELT 2.0 Doc API (https://api.gdeltproject.org/api/v2/doc/doc).
 * GDELT's Doc API is public and does not require an API key, so there is
 * nothing secret to configure here beyond respecting their rate limits.
 */
@Service
@Slf4j
public class GdeltNewsService {

    private static final String GDELT_URL = "https://api.gdeltproject.org/api/v2/doc/doc";
    private static final DateTimeFormatter GDELT_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record ExternalNewsItem(String title, String url, String source, LocalDateTime publishedAt) {}

    /**
     * Searches GDELT for dengue-related articles mentioning the given district name.
     * Returns an empty list (never throws to the caller) if GDELT is unreachable,
     * so a failed sync never breaks the district/alert flow.
     */
    public List<ExternalNewsItem> searchDengueNews(String districtName) {
        List<ExternalNewsItem> results = new ArrayList<>();
        try {
            String query = URLEncoder.encode("dengue " + districtName + " Peru", StandardCharsets.UTF_8);
            String url = GDELT_URL + "?query=" + query
                    + "&mode=artlist&format=json&maxrecords=10&sort=hybridrel";

            String rawResponse = restClient.get()
                    .uri(url)
                    .header("User-Agent", "DengueTrace/1.0")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            for (JsonNode article : root.path("articles")) {
                LocalDateTime publishedAt = parseDate(article.path("seendate").asText(null));
                results.add(new ExternalNewsItem(
                        article.path("title").asText("Sin título"),
                        article.path("url").asText(),
                        article.path("domain").asText("GDELT"),
                        publishedAt
                ));
            }
        } catch (Exception e) {
            log.error("GDELT lookup failed for district '{}': {}", districtName, e.getMessage());
        }
        return results;
    }

    private LocalDateTime parseDate(String raw) {
        try {
            return raw == null ? LocalDateTime.now() : LocalDateTime.parse(raw, GDELT_DATE);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
