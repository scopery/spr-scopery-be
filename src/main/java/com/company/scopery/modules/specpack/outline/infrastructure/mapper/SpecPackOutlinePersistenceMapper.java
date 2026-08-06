package com.company.scopery.modules.specpack.outline.infrastructure.mapper;

import com.company.scopery.modules.specpack.outline.domain.enums.OutlineStatus;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;
import com.company.scopery.modules.specpack.outline.infrastructure.persistence.SpecPackOutlineJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpecPackOutlinePersistenceMapper {

    private static final Logger log = LoggerFactory.getLogger(SpecPackOutlinePersistenceMapper.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public SpecPackOutlinePersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SpecPackOutlineJpaEntity toJpaEntity(SpecPackOutline domain) {
        SpecPackOutlineJpaEntity entity = new SpecPackOutlineJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setVersionNumber(domain.versionNumber());
        entity.setOutlineJson(toJson(domain.outlineJson()));
        entity.setStatus(domain.status().name());
        entity.setApprovedAt(domain.approvedAt());
        return entity;
    }

    public SpecPackOutline toDomain(SpecPackOutlineJpaEntity entity) {
        return SpecPackOutline.reconstitute(
                entity.getId(),
                entity.getSessionId(),
                entity.getVersionNumber(),
                fromJsonMap(entity.getOutlineJson()),
                OutlineStatus.valueOf(entity.getStatus()),
                entity.getApprovedAt(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize outline JSON", e);
            return "{}";
        }
    }

    private Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize outline JSON", e);
            return Map.of();
        }
    }
}
