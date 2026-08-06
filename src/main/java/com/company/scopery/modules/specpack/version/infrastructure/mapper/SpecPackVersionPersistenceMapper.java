package com.company.scopery.modules.specpack.version.infrastructure.mapper;

import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersion;
import com.company.scopery.modules.specpack.version.infrastructure.persistence.SpecPackVersionJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SpecPackVersionPersistenceMapper {

    private static final Logger log = LoggerFactory.getLogger(SpecPackVersionPersistenceMapper.class);
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public SpecPackVersionPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SpecPackVersionJpaEntity toJpaEntity(SpecPackVersion domain) {
        SpecPackVersionJpaEntity entity = new SpecPackVersionJpaEntity();
        entity.setId(domain.id());
        entity.setSpecPackId(domain.specPackId());
        entity.setVersionNumber(domain.versionNumber());
        entity.setSnapshotJson(toJson(domain.snapshotJson()));
        entity.setOutlineJson(toJson(domain.outlineJson()));
        entity.setBlockCount(domain.blockCount());
        entity.setAssetCount(domain.assetCount());
        entity.setChangeReason(domain.changeReason());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPackVersion toDomain(SpecPackVersionJpaEntity entity) {
        return SpecPackVersion.reconstitute(
                entity.getId(),
                entity.getSpecPackId(),
                entity.getVersionNumber(),
                fromJsonList(entity.getSnapshotJson()),
                fromJsonMap(entity.getOutlineJson()),
                entity.getBlockCount(),
                entity.getAssetCount(),
                entity.getChangeReason(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize to JSON", e);
            return "[]";
        }
    }

    private List<Map<String, Object>> fromJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, LIST_MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize snapshot JSON", e);
            return List.of();
        }
    }

    private Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize outline JSON", e);
            return null;
        }
    }
}
