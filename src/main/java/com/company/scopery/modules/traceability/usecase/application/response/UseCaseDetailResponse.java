package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.List;

public record UseCaseDetailResponse(
        UseCaseResponse overview,
        List<UseCaseFlowResponse> flows,
        List<UseCaseConditionResponse> conditions,
        List<UseCaseBusinessRuleResponse> businessRules,
        List<UseCaseAcceptanceCriterionResponse> acceptanceCriteria,
        List<SupportingFunctionResponse> supportingFunctions
) {}
