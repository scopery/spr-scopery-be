package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUseCaseRequest(
        String primaryFunctionId,
        @NotBlank String key,
        @NotBlank String name,
        String goal,
        String primaryActorName,
        String triggerText,
        /** Optional — applied by BE after shell create (single + bulk). */
        List<NestedUseCasePartsRequest.NestedFlowRequest> flows,
        List<NestedUseCasePartsRequest.NestedConditionRequest> conditions,
        List<NestedUseCasePartsRequest.NestedBusinessRuleRequest> businessRules,
        List<NestedUseCasePartsRequest.NestedAcceptanceCriterionRequest> acceptanceCriteria
) {
    /** Compact ctor for shell-only callers / tests. */
    public CreateUseCaseRequest(
            String primaryFunctionId,
            String key,
            String name,
            String goal,
            String primaryActorName,
            String triggerText
    ) {
        this(primaryFunctionId, key, name, goal, primaryActorName, triggerText,
                null, null, null, null);
    }
}
