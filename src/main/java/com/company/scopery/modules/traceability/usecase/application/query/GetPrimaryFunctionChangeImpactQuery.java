package com.company.scopery.modules.traceability.usecase.application.query;

import java.util.UUID;

public record GetPrimaryFunctionChangeImpactQuery(
        UUID projectId,
        UUID useCaseId,
        UUID newFunctionId
) {}
