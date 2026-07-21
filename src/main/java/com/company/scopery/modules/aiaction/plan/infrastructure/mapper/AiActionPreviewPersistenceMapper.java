package com.company.scopery.modules.aiaction.plan.infrastructure.mapper;

import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreview;
import com.company.scopery.modules.aiaction.plan.infrastructure.persistence.AiActionPreviewJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionPreviewPersistenceMapper {

    public AiActionPreviewJpaEntity toJpaEntity(AiActionPreview domain) {
        AiActionPreviewJpaEntity entity = new AiActionPreviewJpaEntity();
        entity.setId(domain.id());
        entity.setPlanId(domain.planId());
        entity.setPreviewHash(domain.previewHash());
        entity.setMaskedDiffJson(domain.maskedDiffJson());
        entity.setWarningsJson(domain.warningsJson());
        entity.setBaselineImpact(domain.baselineImpact());
        entity.setExternalSideEffect(domain.externalSideEffect());
        entity.setValidUntil(domain.validUntil());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionPreview toDomain(AiActionPreviewJpaEntity entity) {
        return AiActionPreview.reconstitute(
                entity.getId(),
                entity.getPlanId(),
                entity.getPreviewHash(),
                entity.getMaskedDiffJson(),
                entity.getWarningsJson(),
                entity.getBaselineImpact(),
                entity.isExternalSideEffect(),
                entity.getValidUntil(),
                entity.getCreatedAt()
        );
    }
}
