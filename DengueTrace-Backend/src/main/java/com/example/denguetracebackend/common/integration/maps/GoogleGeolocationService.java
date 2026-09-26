package com.example.denguetracebackend.common.integration.maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class GoogleGeolocationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.external.google-maps.api-key:}")
    private String apiKey;

    public Optional<GeolocationResult> locate() {

        if (apiKey == null || apiKey.isBlank()) {
            System.out.println(
                    "Google Maps API key not configured. "
                            + "Geolocation disabled."
            );

            return Optional.empty();
        }

        String url =
                "https://www.googleapis.com/geolocation/v1/geolocate?key="
                        + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body =
                Map.of(
                        "considerIp", true
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        try {

            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            request,
                            Map.class
                    );

            Map<String, Object> responseBody =
                    response.getBody();

            if (responseBody == null) {
                return Optional.empty();
            }

            Map<String, Object> location =
                    (Map<String, Object>)
                            responseBody.get("location");

            if (location == null) {
                return Optional.empty();
            }

            double latitude =
                    ((Number) location.get("lat"))
                            .doubleValue();

            double longitude =
                    ((Number) location.get("lng"))
                            .doubleValue();

            double accuracy = 0;

            if (responseBody.get("accuracy") != null) {
                accuracy =
                        ((Number)
                                responseBody.get("accuracy"))
                                .doubleValue();
            }

            return Optional.of(
                    new GeolocationResult(
                            latitude,
                            longitude,
                            accuracy
                    )
            );

        } catch (Exception e) {

            System.out.println(
                    "Google Geolocation error: "
                            + e.getMessage()
            );

            return Optional.empty();
        }
    }

    public record GeolocationResult(
            double latitude,
            double longitude,
            double accuracy
    ) {
    }
}