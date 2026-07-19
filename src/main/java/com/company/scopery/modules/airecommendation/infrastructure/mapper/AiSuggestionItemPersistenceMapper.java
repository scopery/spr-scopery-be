package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionOperation;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionItemJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Map;

@Component
public class AiSuggestionItemPersistenceMapper {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionItemPersistenceMapper.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public AiSuggestionItemPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiSuggestionItem toDomain(AiSuggestionItemJpaEntity entity) {
        return new AiSuggestionItem(
                entity.getId(),
                entity.getSuggestionId(),
                entity.getOrdinal(),
                SuggestionOperation.valueOf(entity.getOperation()),
                entity.getTargetEntityType(),
                entity.getTargetEntityId(),
                entity.getExpectedTargetVersionToken(),
                entity.getSchemaCode(),
                entity.getSchemaVersion(),
                parseMap(entity.getProposedPayload()),
                parseMap(entity.getMaskedBeforeSnapshot()),
                entity.getPayloadHash(),
                entity.getRequiredTargetCapabilityCode(),
                entity.isConfirmationRequired(),
                entity.getBaselineImpact() != null ? BaselineImpact.valueOf(entity.getBaselineImpact()) : null,
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }

    public AiSuggestionItemJpaEntity toJpaEntity(AiSuggestionItem domain) {
        AiSuggestionItemJpaEntity entity = new AiSuggestionItemJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setOrdinal(domain.ordinal());
        entity.setOperation(domain.operation().name());
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setTargetEntityId(domain.targetEntityId());
        entity.setExpectedTargetVersionToken(domain.expectedTargetVersionToken());
        entity.setSchemaCode(domain.schemaCode());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setProposedPayload(writeMap(domain.proposedPayload()));
        entity.setMaskedBeforeSnapshot(writeMap(domain.maskedBeforeSnapshot()));
        entity.setPayloadHash(domain.payloadHash());
        entity.setRequiredTargetCapabilityCode(domain.requiredTargetCapabilityCode());
        entity.setConfirmationRequired(domain.confirmationRequired());
        entity.setBaselineImpact(domain.baselineImpact() != null ? domain.baselineImpact().name() : null);
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }

    private Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON map field: {}", e.getMessage());
            return null;
        }
    }

    private String writeMap(Map<String, Object> map) {
        if (map == null) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize map field: {}", e.getMessage());
            return null;
        }
    }
}
