package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AiRecommendationPolicy(
        UUID id,
        String code,
        String name,
        String description,
        PolicyStatus status,
        String scopeType,
        List<String> triggerModes,
        List<String> packCodes,
        boolean llmEnrichmentEnabled,
        BigDecimal minConfidence,
        String defaultSeverity,
        int defaultCooldownMinutes,
        int maxSuggestionsPerRun,
        boolean publishToInbox,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version
) {}
