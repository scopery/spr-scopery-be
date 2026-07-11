package com.company.scopery.modules.project.wbs.application.command;

import java.util.UUID;

public record CreateWbsNodeCommand(
        UUID projectId,
        UUID projectPhaseId,
        UUID parentId,
        String code,
        String title,
        String description,
        String nodeType,
        int sortOrder
) {}
