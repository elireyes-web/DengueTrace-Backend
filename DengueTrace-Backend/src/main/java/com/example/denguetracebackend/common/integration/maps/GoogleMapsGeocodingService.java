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
 * Wraps the Google Maps Geocoding API.
 *
 * Used to:
 * - Auto-fill latitude/longitude for a District.
 * - Convert latitude/longitude back into district/province/department.
 *
 * Requires GOOGLE_MAPS_API_KEY as an environment variable.
 */
@Service
@Slf4j
public class GoogleMapsGeocodingService {

    private static final String GEOCODE_URL =
            "https://maps.googleapis.com/maps/api/geocode/json";

    private final RestClient restClient = RestClient.create();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Value("${app.external.google-maps.api-key:}")
    private String apiKey;

    /**
     * Result of normal geocoding:
     *
     * district name
     *      ↓
     * latitude / longitude
     */
    public record Coordinates(
            double latitude,
            double longitude
    ) {
    }

    /**
     * Result of reverse geocoding:
     *
     * latitude / longitude
     *      ↓
     * district / province / department
     */
    public record AddressResult(
            String district,
            String province,
            String department,
            double latitude,
            double longitude
    ) {
    }

    /**
     * Geocodes:
     *
     * "name, province, department, Peru"
     *
     * into latitude / longitude.
     */
    public Optional<Coordinates> geocode(
            String name,
            String province,
            String department
    ) {

        if (apiKey == null || apiKey.isBlank()) {

            log.warn(
                    "GOOGLE_MAPS_API_KEY not configured; "
                            + "skipping geocoding for {}",
                    name
            );

            return Optional.empty();
        }

        String address =
                String.join(
                        ", ",
                        java.util.stream.Stream
                                .of(
                                        name,
                                        province,
                                        department,
                                        "Peru"
                                )
                                .filter(
                                        s ->
                                                s != null
                                                        && !s.isBlank()
                                )
                                .toList()
                );

        try {

            String url =
                    GEOCODE_URL
                            + "?address="
                            + URLEncoder.encode(
                            address,
                            StandardCharsets.UTF_8
                    )
                            + "&key="
                            + apiKey;

            String rawResponse =
                    restClient
                            .get()
                            .uri(url)
                            .retrieve()
                            .body(String.class);

            JsonNode root =
                    objectMapper.readTree(
                            rawResponse
                    );

            String status =
                    root.path("status")
                            .asText();

            if (!"OK".equals(status)) {

                log.warn(
                        "Geocoding for '{}' returned status {}",
                        address,
                        status
                );

                return Optional.empty();
            }

            JsonNode results =
                    root.path("results");

            if (!results.isArray()
                    || results.isEmpty()) {

                return Optional.empty();
            }

            JsonNode location =
                    results
                            .get(0)
                            .path("geometry")
                            .path("location");

            double latitude =
                    location
                            .path("lat")
                            .asDouble();

            double longitude =
                    location
                            .path("lng")
                            .asDouble();

            return Optional.of(
                    new Coordinates(
                            latitude,
                            longitude
                    )
            );

        } catch (Exception e) {

            log.error(
                    "Geocoding failed for '{}': {}",
                    address,
                    e.getMessage()
            );

            return Optional.empty();
        }
    }

    /**
     * Reverse geocoding:
     *
     * latitude / longitude
     *      ↓
     * district / province / department
     */
    public Optional<AddressResult> reverseGeocode(
            double latitude,
            double longitude
    ) {

        if (apiKey == null || apiKey.isBlank()) {

            log.warn(
                    "GOOGLE_MAPS_API_KEY not configured; "
                            + "reverse geocoding skipped"
            );

            return Optional.empty();
        }

        try {

            String url =
                    GEOCODE_URL
                            + "?latlng="
                            + latitude
                            + ","
                            + longitude
                            + "&language=es"
                            + "&key="
                            + apiKey;

            String rawResponse =
                    restClient
                            .get()
                            .uri(url)
                            .retrieve()
                            .body(String.class);

            JsonNode root =
                    objectMapper.readTree(
                            rawResponse
                    );

            String status =
                    root.path("status")
                            .asText();

            if (!"OK".equals(status)) {

                log.warn(
                        "Reverse geocoding returned status {} "
                                + "for {}, {}",
                        status,
                        latitude,
                        longitude
                );

                return Optional.empty();
            }

            JsonNode results =
                    root.path("results");

            if (!results.isArray()
                    || results.isEmpty()) {

                log.warn(
                        "Reverse geocoding returned no results "
                                + "for {}, {}",
                        latitude,
                        longitude
                );

                return Optional.empty();
            }

            String district = null;
            String province = null;
            String department = null;

            /*
             * Google can return the same administrative
             * information in different result objects,
             * so we inspect all of them.
             */
            for (JsonNode result : results) {

                JsonNode components =
                        result.path(
                                "address_components"
                        );

                if (!components.isArray()) {
                    continue;
                }

                for (JsonNode component
                        : components) {

                    String longName =
                            component
                                    .path("long_name")
                                    .asText();

                    JsonNode types =
                            component.path("types");

                    /*
                     * Departamento
                     */
                    if (
                            department == null
                                    && containsType(
                                    types,
                                    "administrative_area_level_1"
                            )
                    ) {

                        department = longName;
                    }

                    /*
                     * Provincia
                     */
                    if (
                            province == null
                                    && containsType(
                                    types,
                                    "administrative_area_level_2"
                            )
                    ) {

                        province = longName;
                    }

                    /*
                     * Distrito.
                     *
                     * Google puede clasificarlo de
                     * diferentes maneras dependiendo
                     * de la ubicación.
                     */
                    if (district == null) {

                        if (
                                containsType(
                                        types,
                                        "administrative_area_level_3"
                                )
                        ) {

                            district = longName;

                        } else if (
                                containsType(
                                        types,
                                        "sublocality_level_1"
                                )
                        ) {

                            district = longName;

                        } else if (
                                containsType(
                                        types,
                                        "locality"
                                )
                        ) {

                            district = longName;
                        }
                    }
                }
            }

            if (district == null
                    || district.isBlank()) {

                log.warn(
                        "Could not determine district "
                                + "for {}, {}",
                        latitude,
                        longitude
                );

                return Optional.empty();
            }

            log.info(
                    "Reverse geocoding: {}, {} -> "
                            + "district={}, province={}, department={}",
                    latitude,
                    longitude,
                    district,
                    province,
                    department
            );

            return Optional.of(
                    new AddressResult(
                            district,
                            province,
                            department,
                            latitude,
                            longitude
                    )
            );

        } catch (Exception e) {

            log.error(
                    "Reverse geocoding failed "
                            + "for {}, {}: {}",
                    latitude,
                    longitude,
                    e.getMessage()
            );

            return Optional.empty();
        }
    }

    /**
     * Checks whether Google's "types" array
     * contains a specific value.
     */
    private boolean containsType(
            JsonNode types,
            String expectedType
    ) {

        if (types == null
                || !types.isArray()) {

            return false;
        }

        for (JsonNode type : types) {

            if (
                    expectedType.equals(
                            type.asText()
                    )
            ) {

                return true;
            }
        }

        return false;
    }
}
