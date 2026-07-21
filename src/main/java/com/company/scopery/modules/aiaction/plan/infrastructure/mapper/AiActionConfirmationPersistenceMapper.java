package com.company.scopery.modules.aiaction.plan.infrastructure.mapper;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionConfirmationDecision;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionConfirmationStatus;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmation;
import com.company.scopery.modules.aiaction.plan.infrastructure.persistence.AiActionConfirmationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionConfirmationPersistenceMapper {

    public AiActionConfirmationJpaEntity toJpaEntity(AiActionConfirmation domain) {
        AiActionConfirmationJpaEntity entity = new AiActionConfirmationJpaEntity();
        entity.setId(domain.id());
        entity.setPlanId(domain.planId());
        entity.setPlanVersion(domain.planVersion());
        entity.setPlanHash(domain.planHash());
        entity.setConfirmedByUserId(domain.confirmedByUserId());
        entity.setDecision(domain.decision().name());
        entity.setChannel(domain.channel());
        entity.setComment(domain.comment());
        entity.setConfirmationHash(domain.confirmationHash());
        entity.setStatus(domain.status().name());
        entity.setExpiresAt(domain.expiresAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionConfirmation toDomain(AiActionConfirmationJpaEntity entity) {
        return AiActionConfirmation.reconstitute(
                entity.getId(),
                entity.getPlanId(),
                entity.getPlanVersion(),
                entity.getPlanHash(),
                entity.getConfirmedByUserId(),
                AiActionConfirmationDecision.valueOf(entity.getDecision()),
                entity.getChannel(),
                entity.getComment(),
                entity.getConfirmationHash(),
                AiActionConfirmationStatus.valueOf(entity.getStatus()),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
