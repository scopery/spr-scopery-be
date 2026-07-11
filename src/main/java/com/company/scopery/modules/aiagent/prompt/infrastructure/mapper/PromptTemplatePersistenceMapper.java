package com.company.scopery.modules.aiagent.prompt.infrastructure.mapper;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;
import com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity.PromptTemplateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplatePersistenceMapper {

    public PromptTemplateJpaEntity toJpaEntity(PromptTemplate template) {
        PromptTemplateJpaEntity entity = new PromptTemplateJpaEntity();
        entity.setId(template.id());
        entity.setAgentId(template.agentId());
        entity.setName(template.name());
        entity.setCode(template.code().value());
        entity.setDescription(template.description());
        entity.setStatus(template.status().name());
        if (template.createdAt() != null) {
            entity.setCreatedAt(template.createdAt());
        }
        return entity;
    }

    public PromptTemplate toDomain(PromptTemplateJpaEntity entity) {
        return PromptTemplate.reconstitute(
                entity.getId(),
                entity.getAgentId(),
                entity.getName(),
                PromptTemplateCode.of(entity.getCode()),
                entity.getDescription(),
                PromptTemplateStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}