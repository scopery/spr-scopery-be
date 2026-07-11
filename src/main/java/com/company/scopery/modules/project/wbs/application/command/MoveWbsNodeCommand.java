package com.company.scopery.modules.project.wbs.application.command;

import java.util.UUID;

public record MoveWbsNodeCommand(
        UUID id,
        UUID projectId,
        UUID newParentId,
        int newSortOrder
) {}
