package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.SchemaStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionOperation;
import com.company.scopery.modules.airecommendation.domain.model.SuggestionSchemaDefinition;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.SuggestionSchemaDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class SuggestionSchemaDefinitionPersistenceMapper {

    public SuggestionSchemaDefinition toDomain(SuggestionSchemaDefinitionJpaEntity entity) {
        return new SuggestionSchemaDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getSchemaVersion(),
                entity.getSuggestionType(),
                entity.getOperation() != null ? SuggestionOperation.valueOf(entity.getOperation()) : null,
                entity.getTargetEntityType(),
                entity.getRequiredTargetCapabilityCode(),
                entity.isConfirmationRequired(),
                entity.getBaselineImpact() != null ? BaselineImpact.valueOf(entity.getBaselineImpact()) : null,
                parseList(entity.getSensitiveFieldPaths()),
                entity.getJsonSchema(),
                SchemaStatus.valueOf(entity.getStatus()),
                entity.isImmutableAfterActivation(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersionLock()
        );
    }

    public SuggestionSchemaDefinitionJpaEntity toJpaEntity(SuggestionSchemaDefinition domain) {
        SuggestionSchemaDefinitionJpaEntity entity = new SuggestionSchemaDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setSuggestionType(domain.suggestionType());
        entity.setOperation(domain.operation() != null ? domain.operation().name() : null);
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setRequiredTargetCapabilityCode(domain.requiredTargetCapabilityCode());
        entity.setConfirmationRequired(domain.confirmationRequired());
        entity.setBaselineImpact(domain.baselineImpact() != null ? domain.baselineImpact().name() : null);
        entity.setSensitiveFieldPaths(joinList(domain.sensitiveFieldPaths()));
        entity.setJsonSchema(domain.jsonSchema());
        entity.setStatus(domain.status().name());
        entity.setImmutableAfterActivation(domain.immutableAfterActivation());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }

    private List<String> parseList(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }
}
