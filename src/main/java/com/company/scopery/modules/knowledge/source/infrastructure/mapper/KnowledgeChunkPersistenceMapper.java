package com.company.scopery.modules.knowledge.source.infrastructure.mapper;

import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunk;
import com.company.scopery.modules.knowledge.source.infrastructure.persistence.KnowledgeChunkJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class KnowledgeChunkPersistenceMapper {

    private static final TypeReference<List<String>> LIST_STRING_TYPE = new TypeReference<>() {};
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public KnowledgeChunkPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public KnowledgeChunk toDomain(KnowledgeChunkJpaEntity entity) {
        return new KnowledgeChunk(
                entity.getId(),
                entity.getSourceId(),
                entity.getProjectionId(),
                entity.getChunkOrdinal(),
                entity.getStrategyVersion(),
                entity.getChunkType(),
                parseHeadingPath(entity.getHeadingPath()),
                entity.getPlainText(),
                entity.getTokenCount(),
                entity.getStartCodePoint(),
                entity.getEndCodePoint(),
                entity.getContentHash(),
                parseMetadata(entity.getMetadata()),
                Boolean.TRUE.equals(entity.getIsCurrent()),
                entity.getCreatedAt()
        );
    }

    public KnowledgeChunkJpaEntity toJpaEntity(KnowledgeChunk domain) {
        KnowledgeChunkJpaEntity entity = new KnowledgeChunkJpaEntity();
        entity.setId(domain.id());
        entity.setSourceId(domain.sourceId());
        entity.setProjectionId(domain.projectionId());
        entity.setChunkOrdinal(domain.chunkOrdinal());
        entity.setStrategyVersion(domain.strategyVersion());
        entity.setChunkType(domain.chunkType());
        entity.setHeadingPath(serializeList(domain.headingPath()));
        entity.setPlainText(domain.plainText());
        entity.setTokenCount(domain.tokenCount());
        entity.setStartCodePoint(domain.startCodePoint());
        entity.setEndCodePoint(domain.endCodePoint());
        entity.setContentHash(domain.contentHash());
        entity.setMetadata(serializeMap(domain.metadata()));
        entity.setIsCurrent(domain.isCurrent());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    private List<String> parseHeadingPath(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, LIST_STRING_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Object> parseMetadata(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    private String serializeList(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : Collections.emptyList());
        } catch (JsonProcessingException e) {
            return "[]";
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
