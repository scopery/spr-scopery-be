package com.company.scopery.modules.quality.nfrspecification.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
public record SaveNfrSpecificationRequest(
        @NotBlank @Schema(allowableValues = {"PERFORMANCE","SECURITY","AVAILABILITY","RELIABILITY","SCALABILITY",
                "USABILITY","ACCESSIBILITY","COMPATIBILITY","MAINTAINABILITY","OBSERVABILITY","DATA_INTEGRITY","COMPLIANCE"})
        String qualityAttribute,
        String metricName,
        @Schema(allowableValues = {"LT","LTE","GT","GTE","EQ","BETWEEN"}) String comparisonOperator,
        BigDecimal targetValue, BigDecimal secondaryTargetValue,
        String unit, String measurementWindow,
        String environment, String verificationFrequency,
        String configurationJson) {}
