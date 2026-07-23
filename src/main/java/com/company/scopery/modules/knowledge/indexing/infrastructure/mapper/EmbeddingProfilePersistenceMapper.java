package com.company.scopery.modules.knowledge.indexing.infrastructure.mapper;

import com.company.scopery.modules.knowledge.indexing.domain.model.EmbeddingProfile;
import com.company.scopery.modules.knowledge.indexing.infrastructure.persistence.EmbeddingProfileJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class EmbeddingProfilePersistenceMapper {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public EmbeddingProfilePersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EmbeddingProfile toDomain(EmbeddingProfileJpaEntity entity) {
        return new EmbeddingProfile(
                entity.getId(),
                entity.getCode(),
                entity.getProvider(),
                entity.getModel(),
                entity.getDimensions(),
                entity.getMaxInputTokens(),
                entity.getDistanceMetric(),
                entity.getNormalization(),
                entity.getProfileVersion(),
                entity.getStatus(),
                parseNonSecretConfig(entity.getNonSecretConfig()),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion() != null ? entity.getVersion() : 0L
        );
    }

    public EmbeddingProfileJpaEntity toJpaEntity(EmbeddingProfile domain) {
        EmbeddingProfileJpaEntity entity = new EmbeddingProfileJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setProvider(domain.provider());
        entity.setModel(domain.model());
        entity.setDimensions(domain.dimensions());
        entity.setMaxInputTokens(domain.maxInputTokens());
        entity.setDistanceMetric(domain.distanceMetric());
        entity.setNormalization(domain.normalization());
        entity.setProfileVersion(domain.profileVersion());
        entity.setStatus(domain.status());
        entity.setNonSecretConfig(serializeMap(domain.nonSecretConfig()));
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            if (domain.version() > 0) {
                entity.setVersion(domain.version());
            }
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

    private Map<String, Object> parseNonSecretConfig(String json) {
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
