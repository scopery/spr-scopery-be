package com.company.scopery.modules.traceability.screenspecdoc.application.command;

import java.util.UUID;

public record AddScreenToSpecDocCommand(
        UUID workspaceId,
        UUID documentId,
        UUID screenId,
        int displayOrder,
        String note) {}
