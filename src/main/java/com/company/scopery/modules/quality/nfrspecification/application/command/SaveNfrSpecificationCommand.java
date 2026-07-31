package com.company.scopery.modules.quality.nfrspecification.application.command;
import java.math.BigDecimal; import java.util.UUID;
public record SaveNfrSpecificationCommand(
        UUID projectId, UUID requirementId,
        String qualityAttribute, String metricName,
        String comparisonOperator,
        BigDecimal targetValue, BigDecimal secondaryTargetValue,
        String unit, String measurementWindow,
        String environment, String verificationFrequency,
        String configurationJson) {}
