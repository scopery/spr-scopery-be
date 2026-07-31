package com.company.scopery.modules.traceability.usecase.http.request;

import java.util.UUID;

public record UpdateUseCaseFlowRequest(
        String name,
        UUID sourceStepId,
        String conditionText
) {}
