package com.company.scopery.modules.knowledge.queryrewriter.infrastructure.mapper;

import com.company.scopery.modules.knowledge.queryrewriter.domain.model.KnowledgeQueryRewriterConfig;
import com.company.scopery.modules.knowledge.queryrewriter.infrastructure.persistence.KnowledgeQueryRewriterConfigJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeQueryRewriterConfigPersistenceMapper {

    public KnowledgeQueryRewriterConfig toDomain(KnowledgeQueryRewriterConfigJpaEntity entity) {
        return new KnowledgeQueryRewriterConfig(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.isEnabled(),
                entity.getProvider(),
                entity.getModel(),
                entity.getMaxTokens(),
                entity.getPromptTemplate(),
                entity.getVersion() != null ? entity.getVersion() : 0L,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public KnowledgeQueryRewriterConfigJpaEntity toJpaEntity(KnowledgeQueryRewriterConfig domain) {
        KnowledgeQueryRewriterConfigJpaEntity entity = new KnowledgeQueryRewriterConfigJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setEnabled(domain.enabled());
        entity.setProvider(domain.provider());
        entity.setModel(domain.model());
        entity.setMaxTokens(domain.maxTokens());
        entity.setPromptTemplate(domain.promptTemplate());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            if (domain.version() > 0) {
                entity.setVersion(domain.version());
            }
        }
        return entity;
    }
}
