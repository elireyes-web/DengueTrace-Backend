package com.example.denguetracebackend.common.integration.maps;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class GoogleGeolocationService {

    private final RestClient restClient;

    @Value("${app.external.google-maps.api-key:}")
    private String apiKey;

    public GoogleGeolocationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public Optional<GeolocationResult> locate() {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Google Maps API key not configured. Geolocation disabled.");
            return Optional.empty();
        }

        String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + apiKey;

        try {
            JsonNode response = restClient.post()
                    .uri(url)
                    .body(Map.of("considerIp", true))
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || response.path("location").isMissingNode()) {
                return Optional.empty();
            }

            JsonNode location = response.path("location");
            double latitude = location.path("lat").asDouble();
            double longitude = location.path("lng").asDouble();
            double accuracy = response.path("accuracy").asDouble(0);

            return Optional.of(new GeolocationResult(latitude, longitude, accuracy));
        } catch (Exception e) {
            log.error("Google Geolocation error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public record GeolocationResult(double latitude, double longitude, double accuracy) {}
}
