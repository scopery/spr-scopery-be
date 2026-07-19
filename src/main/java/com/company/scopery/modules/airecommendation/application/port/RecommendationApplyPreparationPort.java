package com.company.scopery.modules.airecommendation.application.port;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RecommendationApplyPreparationPort {

    PrepareApplyResult prepare(PrepareApplyRequest request);

    record PrepareApplyRequest(
            String suggestionRef,
            long suggestionVersion,
            UUID workspaceId,
            UUID projectId,
            UUID actorId,
            List<UUID> selectedItemIds,
            Map<String, String> targetVersionTokens,
            String idempotencyKey,
            UUID originConversationId,
            String traceId
    ) {}

    record PrepareApplyResult(
            boolean available,
            String suggestionRef,
            UUID actionRequestId,
            UUID actionPlanId,
            String planStatus,
            boolean confirmationRequired,
            String expiresAt,
            String unavailableReasonCode
    ) {
        public static PrepareApplyResult unavailable(String suggestionRef) {
            return new PrepareApplyResult(false, suggestionRef, null, null, null, false, null,
                    "PHASE_44_AGENTIC_ACTIONS");
        }
    }
}
