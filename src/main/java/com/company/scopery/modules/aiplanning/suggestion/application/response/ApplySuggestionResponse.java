package com.company.scopery.modules.aiplanning.suggestion.application.response;

import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResult;

import java.util.List;
import java.util.UUID;

public record ApplySuggestionResponse(
        UUID suggestionId,
        boolean changeRequestCreated,
        UUID changeRequestId,
        List<ApplyResultItemResponse> results
) {
    public static ApplySuggestionResponse of(
            UUID suggestionId,
            boolean changeRequestCreated,
            UUID changeRequestId,
            List<AiPlanningApplyResult> results) {
        return new ApplySuggestionResponse(
                suggestionId,
                changeRequestCreated,
                changeRequestId,
                results.stream().map(ApplyResultItemResponse::from).toList());
    }

    public record ApplyResultItemResponse(
            UUID id,
            UUID suggestionItemId,
            String status,
            String domainAction,
            String targetType,
            UUID targetId,
            String errorCode,
            String errorMessage
    ) {
        public static ApplyResultItemResponse from(AiPlanningApplyResult result) {
            return new ApplyResultItemResponse(
                    result.id(),
                    result.suggestionItemId(),
                    result.status().name(),
                    result.domainAction(),
                    result.targetType(),
                    result.targetId(),
                    result.errorCode(),
                    result.errorMessage());
        }
    }
}
