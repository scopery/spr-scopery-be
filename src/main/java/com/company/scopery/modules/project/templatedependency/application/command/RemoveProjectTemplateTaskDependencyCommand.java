package com.company.scopery.modules.project.templatedependency.application.command;

import java.util.UUID;

public record RemoveProjectTemplateTaskDependencyCommand(
        UUID templateId,
        UUID versionId,
        UUID dependencyId
) {}
