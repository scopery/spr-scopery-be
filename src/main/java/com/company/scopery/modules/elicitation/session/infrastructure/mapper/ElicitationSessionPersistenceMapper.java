package com.company.scopery.modules.elicitation.session.infrastructure.mapper;

import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.infrastructure.persistence.ElicitationSessionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ElicitationSessionPersistenceMapper {

    public ElicitationSessionJpaEntity toJpaEntity(ElicitationSession domain) {
        ElicitationSessionJpaEntity entity = new ElicitationSessionJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setScopePackageId(domain.scopePackageId());
        entity.setTitle(domain.title());
        entity.setStatus(domain.status().name());
        entity.setLanguage(domain.language() != null ? domain.language() : "en");
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationSession toDomain(ElicitationSessionJpaEntity entity) {
        return ElicitationSession.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getScopePackageId(),
                entity.getTitle(),
                SessionStatus.valueOf(entity.getStatus()),
                entity.getLanguage(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
