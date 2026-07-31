package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record AddSupportingFunctionCommand(UUID projectId, UUID useCaseId, UUID functionId) {}
