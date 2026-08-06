package com.company.scopery.modules.specpack.clarification.infrastructure.mapper;

import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationPriority;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationSource;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationStatus;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarification;
import com.company.scopery.modules.specpack.clarification.infrastructure.persistence.SpecPackClarificationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SpecPackClarificationPersistenceMapper {

    public SpecPackClarificationJpaEntity toJpaEntity(SpecPackClarification domain) {
        SpecPackClarificationJpaEntity entity = new SpecPackClarificationJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setCode(domain.code());
        entity.setQuestion(domain.question());
        entity.setAnswer(domain.answer());
        entity.setPriority(domain.priority().name());
        entity.setStatus(domain.status().name());
        entity.setSource(domain.source().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPackClarification toDomain(SpecPackClarificationJpaEntity entity) {
        return SpecPackClarification.reconstitute(
                entity.getId(),
                entity.getSessionId(),
                entity.getCode(),
                entity.getQuestion(),
                entity.getAnswer(),
                ClarificationPriority.valueOf(entity.getPriority()),
                ClarificationStatus.valueOf(entity.getStatus()),
                ClarificationSource.valueOf(entity.getSource()),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
