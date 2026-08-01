package com.company.scopery.modules.traceability.usecase.application.query;

import java.util.UUID;

public record ListUseCaseMentionOptionsQuery(
        UUID projectId,
        UUID useCaseId,
        String query,
        String typesCsv,
        UUID screenId,
        int limit,
        String mode
) {}
