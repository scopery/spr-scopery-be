package com.company.scopery.modules.knowledge.source.infrastructure.mapper;

import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeProjection;
import com.company.scopery.modules.knowledge.source.infrastructure.persistence.KnowledgeProjectionJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class KnowledgeProjectionPersistenceMapper {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public KnowledgeProjectionPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KnowledgeProjection toDomain(KnowledgeProjectionJpaEntity entity) {
        return new KnowledgeProjection(
                entity.getId(),
                entity.getSourceId(),
                entity.getProjectionVersion(),
                entity.getExtractorCode(),
                entity.getExtractorVersion(),
                entity.getNormalizationVersion(),
                entity.getPlainText(),
                parseStructuredMetadata(entity.getStructuredMetadata()),
                entity.getContentHash(),
                entity.getProjectionStatus(),
                entity.getFailureCode(),
                entity.getFailureMessageRedacted(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }

    public KnowledgeProjectionJpaEntity toJpaEntity(KnowledgeProjection domain) {
        KnowledgeProjectionJpaEntity entity = new KnowledgeProjectionJpaEntity();
        entity.setId(domain.id());
        entity.setSourceId(domain.sourceId());
        entity.setProjectionVersion(domain.projectionVersion());
        entity.setExtractorCode(domain.extractorCode());
        entity.setExtractorVersion(domain.extractorVersion());
        entity.setNormalizationVersion(domain.normalizationVersion());
        entity.setPlainText(domain.plainText());
        entity.setStructuredMetadata(serializeMap(domain.structuredMetadata()));
        entity.setHeadingIndex("[]");
        entity.setContentHash(domain.contentHash());
        entity.setProjectionStatus(domain.projectionStatus());
        entity.setFailureCode(domain.failureCode());
        entity.setFailureMessageRedacted(domain.failureMessageRedacted());
        entity.setCreatedAt(domain.createdAt());
        entity.setCreatedBy(domain.createdBy());
        return entity;
    }

    private Map<String, Object> parseStructuredMetadata(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    private String serializeMap(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map != null ? map : Collections.emptyMap());
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
