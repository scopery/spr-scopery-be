package com.company.scopery.modules.traceability.specdocrevision.application.command;

import java.util.UUID;

public record DeleteRegistrySpecDocRevisionCommand(
        UUID workspaceId,
        UUID revisionId) {}
