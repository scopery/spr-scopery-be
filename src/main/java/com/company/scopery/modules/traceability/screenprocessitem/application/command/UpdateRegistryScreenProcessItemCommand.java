package com.company.scopery.modules.traceability.screenprocessitem.application.command;

import java.util.UUID;

public record UpdateRegistryScreenProcessItemCommand(
        UUID workspaceId,
        UUID screenId,
        UUID processItemId,
        UUID modeId,
        UUID targetFieldId,
        String title,
        String content,
        String sourceTable,
        String conditionNote,
        int displayOrder) {}
