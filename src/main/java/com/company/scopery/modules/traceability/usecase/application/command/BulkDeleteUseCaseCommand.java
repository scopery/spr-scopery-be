package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record BulkDeleteUseCaseCommand(UUID projectId, List<UUID> ids) {}
