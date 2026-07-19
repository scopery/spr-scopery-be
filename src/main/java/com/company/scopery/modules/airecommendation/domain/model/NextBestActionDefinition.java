package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.ActionKind;
import com.company.scopery.modules.airecommendation.domain.enums.NbaStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record NextBestActionDefinition(
        UUID id,
        String code,
        int version,
        String label,
        String description,
        ActionKind actionKind,
        List<String> applicableSuggestionTypes,
        String requiredAuthorityCode,
        String requiredTargetCapabilityCode,
        String phase44ToolCode,
        String phase44ToolVersion,
        String riskLevel,
        NbaStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long versionLock
) {}
