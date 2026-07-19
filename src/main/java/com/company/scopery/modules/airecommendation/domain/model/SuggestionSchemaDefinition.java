package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.SchemaStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionOperation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SuggestionSchemaDefinition(
        UUID id,
        String code,
        int schemaVersion,
        String suggestionType,
        SuggestionOperation operation,
        String targetEntityType,
        String requiredTargetCapabilityCode,
        boolean confirmationRequired,
        BaselineImpact baselineImpact,
        List<String> sensitiveFieldPaths,
        String jsonSchema,
        SchemaStatus status,
        boolean immutableAfterActivation,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version
) {}
