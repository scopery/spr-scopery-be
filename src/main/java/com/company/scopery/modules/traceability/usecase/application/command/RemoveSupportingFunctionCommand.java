package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record RemoveSupportingFunctionCommand(UUID projectId, UUID useCaseId, UUID functionId) {}
