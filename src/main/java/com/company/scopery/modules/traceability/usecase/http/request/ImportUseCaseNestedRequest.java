package com.company.scopery.modules.traceability.usecase.http.request;

import java.util.List;
import java.util.UUID;

/**
 * One-shot nested import for an existing use case (detail Import panel).
 * Processed entirely by BE — FE must not loop create APIs.
 */
public record ImportUseCaseNestedRequest(
        List<NestedUseCasePartsRequest.NestedFlowRequest> flows,
        List<NestedUseCasePartsRequest.NestedConditionRequest> conditions,
        List<NestedUseCasePartsRequest.NestedBusinessRuleRequest> businessRules,
        List<NestedUseCasePartsRequest.NestedAcceptanceCriterionRequest> acceptanceCriteria,
        List<UUID> supportingFunctionIds
) {}
