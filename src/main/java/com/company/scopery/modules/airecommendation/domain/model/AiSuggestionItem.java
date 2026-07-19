package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionOperation;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record AiSuggestionItem(
        UUID id,
        UUID suggestionId,
        int ordinal,
        SuggestionOperation operation,
        String targetEntityType,
        UUID targetEntityId,
        String expectedTargetVersionToken,
        String schemaCode,
        int schemaVersion,
        Map<String, Object> proposedPayload,
        Map<String, Object> maskedBeforeSnapshot,
        String payloadHash,
        String requiredTargetCapabilityCode,
        boolean confirmationRequired,
        BaselineImpact baselineImpact,
        OffsetDateTime createdAt
) {}
