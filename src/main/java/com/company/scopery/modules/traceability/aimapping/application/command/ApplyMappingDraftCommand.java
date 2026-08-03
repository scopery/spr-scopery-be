package com.company.scopery.modules.traceability.aimapping.application.command;

import java.util.UUID;

public record ApplyMappingDraftCommand(
        UUID projectId,
        UUID runId,
        UUID appliedBy
) {}
