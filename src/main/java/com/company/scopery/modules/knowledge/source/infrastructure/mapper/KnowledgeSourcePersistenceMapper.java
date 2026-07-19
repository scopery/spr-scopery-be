package com.company.scopery.modules.knowledge.source.infrastructure.mapper;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceStatus;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.infrastructure.persistence.KnowledgeSourceJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class KnowledgeSourcePersistenceMapper {

    private static final TypeReference<List<String>> LIST_STRING_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public KnowledgeSourcePersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KnowledgeSource toDomain(KnowledgeSourceJpaEntity entity) {
        return new KnowledgeSource(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                KnowledgeSourceType.valueOf(entity.getSourceType()),
                entity.getSourceRefId(),
                entity.getSourceVersionRefId(),
                entity.getTitle(),
                entity.getLanguage(),
                entity.getClassification(),
                entity.getContentHash(),
                entity.getPermissionSignature(),
                parseAclTokens(entity.getAclTokens()),
                KnowledgeSourceStatus.valueOf(entity.getSourceStatus()),
                entity.getLastObservedAt(),
                entity.getLastIndexedAt(),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion() != null ? entity.getVersion() : 0L
        );
    }

    public KnowledgeSourceJpaEntity toJpaEntity(KnowledgeSource domain) {
        KnowledgeSourceJpaEntity entity = new KnowledgeSourceJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setSourceType(domain.sourceType().name());
        entity.setSourceRefId(domain.sourceRefId());
        entity.setSourceVersionRefId(domain.sourceVersionRefId());
        entity.setTitle(domain.title());
        entity.setLanguage(domain.language());
        entity.setClassification(domain.classification());
        entity.setContentHash(domain.contentHash());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setAclTokens(serializeAclTokens(domain.aclTokens()));
        entity.setSourceStatus(domain.status().name());
        entity.setLastObservedAt(domain.lastObservedAt());
        entity.setLastIndexedAt(domain.lastIndexedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }

    private UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<String> parseAclTokens(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, LIST_STRING_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String serializeAclTokens(List<String> aclTokens) {
        try {
            return objectMapper.writeValueAsString(aclTokens != null ? aclTokens : Collections.emptyList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
