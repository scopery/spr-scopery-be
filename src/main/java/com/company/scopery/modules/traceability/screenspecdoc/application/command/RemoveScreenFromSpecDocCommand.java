package com.company.scopery.modules.traceability.screenspecdoc.application.command;

import java.util.UUID;

public record RemoveScreenFromSpecDocCommand(
        UUID workspaceId,
        UUID documentId,
        UUID screenId) {}
