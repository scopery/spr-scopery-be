package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateUseCaseCommand(UUID projectId, List<CreateUseCaseCommand> items) {}
