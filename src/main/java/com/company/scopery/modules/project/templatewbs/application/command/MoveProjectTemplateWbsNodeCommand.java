package com.company.scopery.modules.project.templatewbs.application.command;

import java.util.UUID;

public record MoveProjectTemplateWbsNodeCommand(
        UUID templateId,
        UUID versionId,
        UUID templateWbsNodeId,
        UUID newParentId,
        int newOrderIndex
) {}
