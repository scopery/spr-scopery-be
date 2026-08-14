package com.company.scopery.modules.traceability.screenspecdoc.application.command;

import java.util.UUID;

public record DeleteRegistryScreenSpecDocCommand(
        UUID workspaceId,
        UUID documentId) {}
