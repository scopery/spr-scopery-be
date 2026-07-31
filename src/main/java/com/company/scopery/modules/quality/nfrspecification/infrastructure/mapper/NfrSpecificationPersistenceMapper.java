package com.company.scopery.modules.quality.nfrspecification.infrastructure.mapper;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.*;
import com.company.scopery.modules.quality.nfrspecification.domain.model.NfrSpecification;
import com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence.NfrSpecificationJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NfrSpecificationPersistenceMapper {
    public NfrSpecification toDomain(NfrSpecificationJpaEntity e) {
        return new NfrSpecification(
                e.getRequirementId(),
                QualityAttribute.valueOf(e.getQualityAttribute()),
                e.getMetricName(),
                e.getComparisonOperator() != null ? ComparisonOperator.valueOf(e.getComparisonOperator()) : null,
                e.getTargetValue(), e.getSecondaryTargetValue(),
                e.getUnit(), e.getMeasurementWindow(), e.getEnvironment(),
                e.getVerificationFrequency(), e.getConfigurationJson(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public NfrSpecificationJpaEntity toJpaEntity(NfrSpecification d) {
        NfrSpecificationJpaEntity e = new NfrSpecificationJpaEntity();
        e.setRequirementId(d.requirementId());
        e.setQualityAttribute(d.qualityAttribute().name());
        e.setMetricName(d.metricName());
        e.setComparisonOperator(d.comparisonOperator() != null ? d.comparisonOperator().name() : null);
        e.setTargetValue(d.targetValue()); e.setSecondaryTargetValue(d.secondaryTargetValue());
        e.setUnit(d.unit()); e.setMeasurementWindow(d.measurementWindow());
        e.setEnvironment(d.environment()); e.setVerificationFrequency(d.verificationFrequency());
        e.setConfigurationJson(d.configurationJson());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
