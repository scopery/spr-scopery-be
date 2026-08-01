package com.company.scopery.modules.traceability.usecase.application.query;

import java.util.UUID;

public record GetUseCaseFlowScopeQuery(UUID projectId, UUID useCaseId) {}
