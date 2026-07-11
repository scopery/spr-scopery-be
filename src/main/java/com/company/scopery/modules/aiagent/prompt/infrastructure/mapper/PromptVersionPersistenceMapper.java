package com.company.scopery.modules.aiagent.prompt.infrastructure.mapper;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity.PromptVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PromptVersionPersistenceMapper {

    public PromptVersionJpaEntity toJpaEntity(PromptVersion version) {
        PromptVersionJpaEntity entity = new PromptVersionJpaEntity();
        entity.setId(version.id());
        entity.setTemplateId(version.templateId());
        entity.setVersionNumber(version.versionNumber());
        entity.setTitle(version.title());
        entity.setContent(version.content());
        entity.setContentFormat(version.contentFormat().name());
        entity.setVariableSchema(version.variableSchema());
        entity.setChangeNote(version.changeNote());
        entity.setStatus(version.status().name());
        if (version.createdAt() != null) {
            entity.setCreatedAt(version.createdAt());
        }
        return entity;
    }

    public PromptVersion toDomain(PromptVersionJpaEntity entity) {
        return PromptVersion.reconstitute(
                entity.getId(),
                entity.getTemplateId(),
                entity.getVersionNumber(),
                entity.getTitle(),
                entity.getContent(),
                PromptContentFormat.valueOf(entity.getContentFormat()),
                entity.getVariableSchema(),
                entity.getChangeNote(),
                PromptVersionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
