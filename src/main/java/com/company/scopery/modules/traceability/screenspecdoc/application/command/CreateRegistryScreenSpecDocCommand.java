package com.company.scopery.modules.traceability.screenspecdoc.application.command;

import java.util.UUID;

public record CreateRegistryScreenSpecDocCommand(
        UUID workspaceId,
        UUID projectId,
        String documentCode,
        String documentName,
        String projectName,
        String systemName,
        String phaseName,
        String language,
        String overview,
        String figmaUrl) {}
