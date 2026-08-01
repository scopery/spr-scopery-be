package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.List;
import java.util.UUID;

public record PrimaryFunctionChangeImpactResponse(
        UUID useCaseId,
        UUID currentFunctionId,
        UUID newFunctionId,
        List<OutOfScopeMention> outOfScopeMentions
) {
    public record OutOfScopeMention(
            String entityType,
            UUID entityId,
            String label,
            UUID screenId,
            UUID stepId
    ) {}
}
