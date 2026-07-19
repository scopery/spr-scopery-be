package com.company.scopery.modules.project.templateversion.application.command;

import java.util.UUID;

public record UpdateProjectTemplateVersionCommand(
        UUID templateId,
        UUID versionId,
        String name,
        String description
) {}
