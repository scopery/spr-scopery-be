package com.company.scopery.modules.specpack.block.infrastructure.mapper;

import com.company.scopery.modules.specpack.block.domain.enums.BlockStatus;
import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.block.infrastructure.persistence.SpecPackBlockJpaEntity;
import com.company.scopery.modules.specpack.block.infrastructure.persistence.SpecPackBlockRevisionJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SpecPackBlockPersistenceMapper {

    private static final Logger log = LoggerFactory.getLogger(SpecPackBlockPersistenceMapper.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public SpecPackBlockPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SpecPackBlockJpaEntity toJpaEntity(SpecPackBlock domain) {
        SpecPackBlockJpaEntity entity = new SpecPackBlockJpaEntity();
        entity.setId(domain.id());
        entity.setSpecPackId(domain.specPackId());
        entity.setBlockKey(domain.blockKey());
        entity.setParentBlockId(domain.parentBlockId());
        entity.setBlockType(domain.blockType().name());
        entity.setTitle(domain.title());
        entity.setContentFormat(domain.contentFormat().name());
        entity.setContentJson(toJson(domain.contentJson()));
        entity.setSourceRefsJson(toJson(domain.sourceRefsJson()));
        entity.setDisplayOrder(domain.displayOrder());
        entity.setStatus(domain.status().name());
        entity.setCurrentRevisionNumber(domain.currentRevisionNumber());
        entity.setDeletedAt(domain.deletedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPackBlock toDomain(SpecPackBlockJpaEntity entity) {
        return SpecPackBlock.reconstitute(
                entity.getId(),
                entity.getSpecPackId(),
                entity.getBlockKey(),
                entity.getParentBlockId(),
                BlockType.valueOf(entity.getBlockType()),
                entity.getTitle(),
                ContentFormat.valueOf(entity.getContentFormat()),
                fromJsonMap(entity.getContentJson()),
                fromJsonList(entity.getSourceRefsJson()),
                entity.getDisplayOrder(),
                BlockStatus.valueOf(entity.getStatus()),
                entity.getCurrentRevisionNumber(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public SpecPackBlockRevisionJpaEntity toRevisionJpaEntity(SpecPackBlockRevision domain) {
        SpecPackBlockRevisionJpaEntity entity = new SpecPackBlockRevisionJpaEntity();
        entity.setId(domain.id());
        entity.setSpecPackBlockId(domain.specPackBlockId());
        entity.setRevisionNumber(domain.revisionNumber());
        entity.setTitle(domain.title());
        entity.setContentFormat(domain.contentFormat().name());
        entity.setContentJson(toJson(domain.contentJson()));
        entity.setSourceRefsJson(toJson(domain.sourceRefsJson()));
        entity.setChangeSource(domain.changeSource().name());
        entity.setChangeComment(domain.changeComment());
        return entity;
    }

    public SpecPackBlockRevision toRevisionDomain(SpecPackBlockRevisionJpaEntity entity) {
        return new SpecPackBlockRevision(
                entity.getId(),
                entity.getSpecPackBlockId(),
                entity.getRevisionNumber(),
                entity.getTitle(),
                ContentFormat.valueOf(entity.getContentFormat()),
                fromJsonMap(entity.getContentJson()),
                fromJsonList(entity.getSourceRefsJson()),
                ChangeSource.valueOf(entity.getChangeSource()),
                entity.getChangeComment(),
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
            return "{}";
        }
    }

    private Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize content JSON", e);
            return Map.of();
        }
    }

    private List<Map<String, Object>> fromJsonList(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, LIST_MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize source refs JSON", e);
            return null;
        }
    }
}
