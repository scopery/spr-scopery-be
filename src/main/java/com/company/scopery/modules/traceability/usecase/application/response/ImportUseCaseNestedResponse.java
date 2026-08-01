package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.UUID;

public record ImportUseCaseNestedResponse(UUID useCaseId, int createdParts) {}
