package com.company.scopery.modules.airecommendation.application.port;

import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;

import java.util.List;
import java.util.UUID;

public interface RecommendationDetector {

    String detectorCode();

    List<SuggestionCandidate> detect(DetectorContext context);

    record DetectorContext(
            UUID workspaceId,
            UUID projectId,
            UUID actorId,
            RecommendationDetectorDefinition detectorDefinition,
            String traceId
    ) {}

    record SuggestionCandidate(
            String suggestionType,
            String schemaCode,
            int schemaVersion,
            String targetEntityType,
            UUID targetEntityId,
            java.util.Map<String, Object> proposedPayload,
            String title,
            String summary,
            String reason,
            String category,
            String riskLevel,
            java.math.BigDecimal confidenceValue,
            com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod confidenceMethod,
            List<EvidenceFact> directEvidence
    ) {}

    record EvidenceFact(
            String sourceType,
            UUID sourceRefId,
            String title,
            String fragment,
            String appRoute
    ) {}
}
