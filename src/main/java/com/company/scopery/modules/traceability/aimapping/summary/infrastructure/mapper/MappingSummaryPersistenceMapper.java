package com.company.scopery.modules.traceability.aimapping.summary.infrastructure.mapper;

import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.company.scopery.modules.traceability.aimapping.summary.infrastructure.persistence.MappingSummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MappingSummaryPersistenceMapper {

    public MappingSummary toDomain(MappingSummaryJpaEntity entity) {
        return new MappingSummary(
                entity.getId(),
                SummaryEntityType.valueOf(entity.getEntityType()),
                entity.getEntityId(),
                entity.getEntityVersion(),
                entity.getCompactText(),
                entity.getStructuredJson(),
                entity.getSummaryHash(),
                entity.getGeneratedAt()
        );
    }

    public MappingSummaryJpaEntity toJpaEntity(MappingSummary domain) {
        MappingSummaryJpaEntity entity = new MappingSummaryJpaEntity();
        entity.setId(domain.id());
        entity.setEntityType(domain.entityType().name());
        entity.setEntityId(domain.entityId());
        entity.setEntityVersion(domain.entityVersion());
        entity.setCompactText(domain.compactText());
        entity.setStructuredJson(domain.structuredJson());
        entity.setSummaryHash(domain.summaryHash());
        entity.setGeneratedAt(domain.generatedAt());
        if (domain.generatedAt() != null) {
            entity.setCreatedAt(domain.generatedAt());
        }
        return entity;
    }
}
