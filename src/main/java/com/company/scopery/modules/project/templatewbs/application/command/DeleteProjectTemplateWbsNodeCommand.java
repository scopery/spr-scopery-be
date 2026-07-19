package com.company.scopery.modules.project.templatewbs.application.command;

import java.util.UUID;

public record DeleteProjectTemplateWbsNodeCommand(
        UUID templateId,
        UUID versionId,
        UUID templateWbsNodeId,
        boolean cascade
) {}
