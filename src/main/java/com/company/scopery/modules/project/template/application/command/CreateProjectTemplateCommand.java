package com.company.scopery.modules.project.template.application.command;

import java.util.UUID;

public record CreateProjectTemplateCommand(
        String code,
        String name,
        String description,
        String scope,
        UUID organizationId,
        UUID workspaceId,
        String category,
        String visibility,
        boolean builtIn
) {}
