package com.company.scopery.modules.quality.nfrspecification.domain.model;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.*;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record NfrSpecification(
        UUID requirementId,
        QualityAttribute qualityAttribute,
        String metricName,
        ComparisonOperator comparisonOperator,
        BigDecimal targetValue,
        BigDecimal secondaryTargetValue,
        String unit,
        String measurementWindow,
        String environment,
        String verificationFrequency,
        String configurationJson,
        Instant createdAt, Instant updatedAt) {

    public static NfrSpecification create(UUID requirementId, QualityAttribute qualityAttribute,
            String metricName, ComparisonOperator comparisonOperator,
            BigDecimal targetValue, BigDecimal secondaryTargetValue, String unit,
            String measurementWindow, String environment, String verificationFrequency,
            String configurationJson) {
        Instant now = Instant.now();
        return new NfrSpecification(requirementId, qualityAttribute, metricName, comparisonOperator,
                targetValue, secondaryTargetValue, unit, measurementWindow, environment,
                verificationFrequency, configurationJson, now, now);
    }

    public NfrSpecification update(QualityAttribute qualityAttribute, String metricName,
            ComparisonOperator comparisonOperator, BigDecimal targetValue, BigDecimal secondaryTargetValue,
            String unit, String measurementWindow, String environment, String verificationFrequency,
            String configurationJson) {
        return new NfrSpecification(requirementId,
                qualityAttribute != null ? qualityAttribute : this.qualityAttribute,
                metricName != null ? metricName : this.metricName,
                comparisonOperator != null ? comparisonOperator : this.comparisonOperator,
                targetValue != null ? targetValue : this.targetValue,
                secondaryTargetValue != null ? secondaryTargetValue : this.secondaryTargetValue,
                unit != null ? unit : this.unit,
                measurementWindow != null ? measurementWindow : this.measurementWindow,
                environment != null ? environment : this.environment,
                verificationFrequency != null ? verificationFrequency : this.verificationFrequency,
                configurationJson != null ? configurationJson : this.configurationJson,
                createdAt, Instant.now());
    }
}
