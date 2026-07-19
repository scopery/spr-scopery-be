package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.DetectorExecutionMethod;
import com.company.scopery.modules.airecommendation.domain.enums.DetectorStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionSeverity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RecommendationDetectorDefinition(
        UUID id,
        String code,
        int version,
        String packCode,
        String suggestionType,
        String schemaCode,
        int schemaVersion,
        DetectorExecutionMethod executionMethod,
        BigDecimal defaultConfidence,
        SuggestionSeverity defaultSeverity,
        int defaultExpiryMinutes,
        int defaultCooldownMinutes,
        boolean nonSuppressible,
        DetectorStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long versionLock
) {}
