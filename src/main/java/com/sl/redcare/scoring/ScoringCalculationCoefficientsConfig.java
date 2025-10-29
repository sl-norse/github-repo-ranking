package com.sl.redcare.scoring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("redcare.interview.calculation.coefficients")
public record ScoringCalculationCoefficientsConfig(Double stars, Double forks, Double updated) {
}
