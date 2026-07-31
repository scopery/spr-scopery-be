package com.company.scopery.modules.quality.nfrspecification.application.response;
import com.company.scopery.modules.quality.nfrspecification.domain.model.NfrSpecification;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record NfrSpecificationResponse(
        UUID requirementId,
        String qualityAttribute, String metricName,
        String comparisonOperator,
        BigDecimal targetValue, BigDecimal secondaryTargetValue,
        String unit, String measurementWindow,
        String environment, String verificationFrequency,
        String configurationJson,
        Instant createdAt, Instant updatedAt) {
    public static NfrSpecificationResponse from(NfrSpecification e) {
        return new NfrSpecificationResponse(
                e.requirementId(),
                e.qualityAttribute().name(), e.metricName(),
                e.comparisonOperator() != null ? e.comparisonOperator().name() : null,
                e.targetValue(), e.secondaryTargetValue(),
                e.unit(), e.measurementWindow(),
                e.environment(), e.verificationFrequency(),
                e.configurationJson(), e.createdAt(), e.updatedAt());
    }
}
