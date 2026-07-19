package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.PackStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RecommendationPackDefinition(
        UUID id,
        String code,
        int version,
        String name,
        String description,
        List<String> detectorCodes,
        List<String> allowedTriggerModes,
        boolean llmEnrichmentEnabled,
        int defaultCooldownMinutes,
        int defaultExpiryMinutes,
        int maxSuggestionsPerRun,
        PackStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long versionLock
) {}
