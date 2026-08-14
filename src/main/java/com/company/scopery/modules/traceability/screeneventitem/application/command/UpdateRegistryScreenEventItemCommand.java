package com.company.scopery.modules.traceability.screeneventitem.application.command;

import java.util.UUID;

public record UpdateRegistryScreenEventItemCommand(
        UUID workspaceId,
        UUID screenId,
        UUID eventItemId,
        UUID modeId,
        UUID triggerFieldId,
        String triggerActionCode,
        String title,
        String content,
        String conditionNote,
        UUID targetScreenId,
        String targetModeCode,
        int displayOrder) {}
