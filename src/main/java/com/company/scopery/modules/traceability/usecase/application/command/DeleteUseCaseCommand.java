package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record DeleteUseCaseCommand(UUID projectId, UUID useCaseId) {}
