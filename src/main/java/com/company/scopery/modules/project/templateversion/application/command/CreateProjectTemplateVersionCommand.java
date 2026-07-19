package com.company.scopery.modules.project.templateversion.application.command;

import java.util.UUID;

public record CreateProjectTemplateVersionCommand(
        UUID templateId,
        String name,
        String description
) {}
