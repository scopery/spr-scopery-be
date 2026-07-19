package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.enums.EvidenceType;
import com.company.scopery.modules.airecommendation.domain.enums.SupportStrength;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AiSuggestionEvidence(
        UUID id,
        UUID suggestionId,
        int ordinal,
        EvidenceType evidenceType,
        SupportStrength supportStrength,
        UUID aiassistantCitationId,
        UUID knowledgeChunkId,
        UUID retrievalTraceId,
        String sourceType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String fieldPath,
        String title,
        String quotedFragment,
        String appRoute,
        String permissionSignature,
        AccessValidationResult accessValidationResult,
        OffsetDateTime accessValidatedAt,
        OffsetDateTime createdAt
) {}
