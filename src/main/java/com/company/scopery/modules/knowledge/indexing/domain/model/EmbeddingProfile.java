package com.company.scopery.modules.knowledge.indexing.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EmbeddingProfile(
        UUID id,
        String code,
        String provider,
        String model,
        int dimensions,
        int maxInputTokens,
        String distanceMetric,
        String normalization,
        int profileVersion,
        String status,
        Map<String, Object> nonSecretConfig,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy,
        long version
) {}
