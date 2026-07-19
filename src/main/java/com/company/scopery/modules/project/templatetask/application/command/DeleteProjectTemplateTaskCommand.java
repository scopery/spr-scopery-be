package com.company.scopery.modules.project.templatetask.application.command;

import java.util.UUID;

public record DeleteProjectTemplateTaskCommand(
        UUID templateId,
        UUID versionId,
        UUID templateTaskId
) {}
