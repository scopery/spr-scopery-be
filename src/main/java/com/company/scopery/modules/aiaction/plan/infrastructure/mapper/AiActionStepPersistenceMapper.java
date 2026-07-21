package com.company.scopery.modules.aiaction.plan.infrastructure.mapper;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.infrastructure.persistence.AiActionStepJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AiActionStepPersistenceMapper {

    private final ObjectMapper objectMapper;

    public AiActionStepPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiActionStepJpaEntity toJpaEntity(AiActionStep domain) {
        AiActionStepJpaEntity entity = new AiActionStepJpaEntity();
        entity.setId(domain.id());
        entity.setPlanId(domain.planId());
        entity.setOrdinal(domain.ordinal());
        entity.setToolCode(domain.toolCode());
        entity.setToolVersion(domain.toolVersion());
        entity.setInputSchemaCode(domain.inputSchemaCode());
        entity.setInputSchemaVersion(domain.inputSchemaVersion());
        entity.setInputHash(domain.inputHash());
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setTargetEntityId(domain.targetEntityId());
        entity.setExpectedTargetVersionToken(domain.expectedTargetVersionToken());
        entity.setRiskLevel(domain.riskLevel() != null ? domain.riskLevel().name() : null);
        entity.setExecutionMode(domain.executionMode() != null ? domain.executionMode().name() : null);
        entity.setDependsOnStepIds(serializeUuidList(domain.dependsOnStepIds()));
        return entity;
    }

    public AiActionStep toDomain(AiActionStepJpaEntity entity) {
        return AiActionStep.reconstitute(
                entity.getId(),
                entity.getPlanId(),
                entity.getOrdinal(),
                entity.getToolCode(),
                entity.getToolVersion(),
                entity.getInputSchemaCode(),
                entity.getInputSchemaVersion(),
                entity.getInputHash(),
                entity.getTargetEntityType(),
                entity.getTargetEntityId(),
                entity.getExpectedTargetVersionToken(),
                entity.getRiskLevel() != null ? AiActionRiskLevel.valueOf(entity.getRiskLevel()) : null,
                entity.getExecutionMode() != null ? AiActionExecutionMode.valueOf(entity.getExecutionMode()) : null,
                deserializeUuidList(entity.getDependsOnStepIds())
        );
    }

    private String serializeUuidList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<UUID> deserializeUuidList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<UUID>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
