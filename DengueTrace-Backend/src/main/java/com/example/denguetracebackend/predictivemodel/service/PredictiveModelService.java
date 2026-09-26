package com.example.denguetracebackend.predictivemodel.service;

import com.example.denguetracebackend.climatedata.entity.ClimateData;
import com.example.denguetracebackend.climatedata.repository.ClimateDataRepository;
import com.example.denguetracebackend.common.enums.RiskLevel;
import com.example.denguetracebackend.common.exception.BusinessRuleViolationException;
import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.historicalcase.entity.HistoricalCase;
import com.example.denguetracebackend.historicalcase.repository.HistoricalCaseRepository;
import com.example.denguetracebackend.predictivemodel.dto.PredictiveModelResponseDTO;
import com.example.denguetracebackend.predictivemodel.entity.PredictiveModel;
import com.example.denguetracebackend.predictivemodel.repository.PredictiveModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictiveModelService {

    private final PredictiveModelRepository predictiveModelRepository;
    private final HistoricalCaseRepository historicalCaseRepository;
    private final ClimateDataRepository climateDataRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public PredictiveModelResponseDTO generate(Long districtId) {

        District district = districtRepository.findById(districtId)
                .orElseThrow(() ->
                        ResourceNotFoundException.of(
                                "District",
                                districtId
                        )
                );

        List<HistoricalCase> historicalCases =
                historicalCaseRepository
                        .findByDistrictIdOrderByWeekStartDateAsc(
                                districtId
                        );

        if (historicalCases.size() < 4) {
            throw new BusinessRuleViolationException(
                    "At least 4 weeks of historical data are required "
                            + "to generate a prediction"
            );
        }

        List<ClimateData> climateData =
                climateDataRepository
                        .findByDistrictIdOrderByRecordedDateAsc(
                                districtId
                        );

        double historicalAverage =
                calculateHistoricalAverage(
                        historicalCases
                );

        double recentAverage =
                calculateRecentAverage(
                        historicalCases
                );

        double trendFactor =
                calculateTrendFactor(
                        historicalCases
                );

        double climateFactor =
                calculateClimateFactor(
                        climateData
                );

        double basePrediction =
                recentAverage
                        * trendFactor
                        * climateFactor;

        int week1 =
                sanitizePrediction(
                        basePrediction
                );

        int week2 =
                sanitizePrediction(
                        basePrediction
                                * trendFactor
                );

        int week3 =
                sanitizePrediction(
                        basePrediction
                                * Math.pow(
                                trendFactor,
                                2
                        )
                );

        int week4 =
                sanitizePrediction(
                        basePrediction
                                * Math.pow(
                                trendFactor,
                                3
                        )
                );

        RiskLevel riskLevel =
                calculateRiskLevel(
                        district,
                        week4
                );

        double confidenceLevel =
                calculateConfidence(
                        historicalCases.size(),
                        climateData.size()
                );

        PredictiveModel model =
                PredictiveModel.builder()
                        .district(district)
                        .historicalAverage(
                                round(historicalAverage)
                        )
                        .recentAverage(
                                round(recentAverage)
                        )
                        .trendFactor(
                                round(trendFactor)
                        )
                        .climateFactor(
                                round(climateFactor)
                        )
                        .projectedWeek1(week1)
                        .projectedWeek2(week2)
                        .projectedWeek3(week3)
                        .projectedWeek4(week4)
                        .riskLevel(riskLevel)
                        .confidenceLevel(
                                round(confidenceLevel)
                        )
                        .build();

        return toResponse(
                predictiveModelRepository.save(
                        model
                )
        );
    }

    @Transactional(readOnly = true)
    public PredictiveModelResponseDTO
    getLatestByDistrict(
            Long districtId
    ) {

        PredictiveModel model =
                predictiveModelRepository
                        .findTopByDistrictIdOrderByGeneratedAtDesc(
                                districtId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No predictive model found "
                                                + "for district: "
                                                + districtId
                                )
                        );

        return toResponse(model);
    }

    @Transactional(readOnly = true)
    public List<PredictiveModelResponseDTO>
    getHistoryByDistrict(
            Long districtId
    ) {

        return predictiveModelRepository
                .findByDistrictIdOrderByGeneratedAtDesc(
                        districtId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private double calculateHistoricalAverage(
            List<HistoricalCase> cases
    ) {

        return cases.stream()
                .mapToInt(
                        HistoricalCase::getCaseCount
                )
                .average()
                .orElse(0);
    }

    private double calculateRecentAverage(
            List<HistoricalCase> cases
    ) {

        int size = cases.size();

        int startIndex =
                Math.max(
                        0,
                        size - 4
                );

        return cases.subList(
                        startIndex,
                        size
                )
                .stream()
                .mapToInt(
                        HistoricalCase::getCaseCount
                )
                .average()
                .orElse(0);
    }

    private double calculateTrendFactor(
            List<HistoricalCase> cases
    ) {

        int size = cases.size();

        if (size < 8) {
            return 1.0;
        }

        double recentAverage =
                cases.subList(
                                size - 4,
                                size
                        )
                        .stream()
                        .mapToInt(
                                HistoricalCase::getCaseCount
                        )
                        .average()
                        .orElse(0);

        double previousAverage =
                cases.subList(
                                size - 8,
                                size - 4
                        )
                        .stream()
                        .mapToInt(
                                HistoricalCase::getCaseCount
                        )
                        .average()
                        .orElse(0);

        if (previousAverage <= 0) {
            return 1.0;
        }

        double factor =
                recentAverage
                        / previousAverage;

        /*
         * Evitamos que un dato atípico haga
         * crecer o caer la proyección sin límite.
         */
        return clamp(
                factor,
                0.75,
                1.35
        );
    }

    private double calculateClimateFactor(
            List<ClimateData> climateData
    ) {

        if (climateData.isEmpty()) {
            return 1.0;
        }

        int size =
                climateData.size();

        int startIndex =
                Math.max(
                        0,
                        size - 30
                );

        List<ClimateData> recent =
                climateData.subList(
                        startIndex,
                        size
                );

        double avgTemperature =
                recent.stream()
                        .mapToDouble(
                                ClimateData::getTemperature
                        )
                        .average()
                        .orElse(0);

        double avgHumidity =
                recent.stream()
                        .mapToDouble(
                                ClimateData::getHumidity
                        )
                        .average()
                        .orElse(0);

        double avgPrecipitation =
                recent.stream()
                        .mapToDouble(
                                ClimateData::getPrecipitationMm
                        )
                        .average()
                        .orElse(0);

        double factor = 1.0;

        /*
         * Valores deliberadamente simples para
         * un modelo académico y explicable.
         */

        if (avgTemperature >= 24
                && avgTemperature <= 32) {

            factor += 0.10;
        }

        if (avgHumidity >= 70) {
            factor += 0.10;
        }

        if (avgPrecipitation >= 5) {
            factor += 0.10;
        }

        return clamp(
                factor,
                0.85,
                1.30
        );
    }

    private RiskLevel calculateRiskLevel(
            District district,
            int projectedCases
    ) {

        if (district.getPopulation() == null
                || district.getPopulation() <= 0) {

            if (projectedCases < 10) {
                return RiskLevel.LOW;
            }

            if (projectedCases < 25) {
                return RiskLevel.MEDIUM;
            }

            if (projectedCases < 50) {
                return RiskLevel.HIGH;
            }

            return RiskLevel.CRITICAL;
        }

        double incidencePer100k =
                (
                        projectedCases
                                * 100000.0
                )
                        / district.getPopulation();

        if (incidencePer100k < 10) {
            return RiskLevel.LOW;
        }

        if (incidencePer100k < 25) {
            return RiskLevel.MEDIUM;
        }

        if (incidencePer100k < 50) {
            return RiskLevel.HIGH;
        }

        return RiskLevel.CRITICAL;
    }

    private double calculateConfidence(
            int historicalCount,
            int climateCount
    ) {

        double historicalScore =
                Math.min(
                        historicalCount / 12.0,
                        1.0
                );

        double climateScore =
                Math.min(
                        climateCount / 30.0,
                        1.0
                );

        /*
         * 70 % depende de datos epidemiológicos
         * y 30 % de los datos climáticos.
         */
        return (
                historicalScore * 0.70
        )
                + (
                climateScore * 0.30
        );
    }

    private int sanitizePrediction(
            double value
    ) {

        return Math.max(
                0,
                (int) Math.round(value)
        );
    }

    private double clamp(
            double value,
            double min,
            double max
    ) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    private double round(
            double value
    ) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }

    private PredictiveModelResponseDTO
    toResponse(
            PredictiveModel model
    ) {

        return new PredictiveModelResponseDTO(

                model.getId(),

                model.getDistrict()
                        .getId(),

                model.getDistrict()
                        .getName(),

                model.getHistoricalAverage(),

                model.getRecentAverage(),

                model.getTrendFactor(),

                model.getClimateFactor(),

                model.getProjectedWeek1(),

                model.getProjectedWeek2(),

                model.getProjectedWeek3(),

                model.getProjectedWeek4(),

                model.getRiskLevel(),

                model.getConfidenceLevel(),

                model.getGeneratedAt()
        );
    }
}