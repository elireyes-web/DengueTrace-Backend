package com.example.denguetracebackend.common.integration.maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Wraps the Google Maps Geocoding API. Used to auto-fill latitude/longitude
 * for a District when an admin creates/updates one without coordinates.
 *
 * Requires GOOGLE_MAPS_API_KEY as an environment variable (never hardcoded,
 * never committed). See PATCH_NOTES.md for how to obtain and configure it.
 */
@Service
@Slf4j
public class GoogleMapsGeocodingService {

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.external.google-maps.api-key:}")
    private String apiKey;

    public record Coordinates(double latitude, double longitude) {}

    /**
     * Geocodes "name, province, department, Peru" into lat/lng.
     * Returns empty if the API key isn't configured or the lookup fails,
     * so district creation never breaks just because geocoding is unavailable.
     */
    public Optional<Coordinates> geocode(String name, String province, String department) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GOOGLE_MAPS_API_KEY not configured; skipping geocoding for {}", name);
            return Optional.empty();
        }

        String address = String.join(", ",
                java.util.stream.Stream.of(name, province, department, "Peru")
                        .filter(s -> s != null && !s.isBlank())
                        .toList());

        try {
            String url = GEOCODE_URL + "?address=" + URLEncoder.encode(address, StandardCharsets.UTF_8)
                    + "&key=" + apiKey;

            String rawResponse = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            String status = root.path("status").asText();
            if (!"OK".equals(status)) {
                log.warn("Geocoding for '{}' returned status {}", address, status);
                return Optional.empty();
            }

            JsonNode location = root.path("results").get(0).path("geometry").path("location");
            return Optional.of(new Coordinates(location.path("lat").asDouble(), location.path("lng").asDouble()));
        } catch (Exception e) {
            log.error("Geocoding failed for '{}': {}", address, e.getMessage());
            return Optional.empty();
        }
    }
}
