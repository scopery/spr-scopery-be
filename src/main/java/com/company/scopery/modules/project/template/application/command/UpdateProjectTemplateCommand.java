package com.company.scopery.modules.project.template.application.command;

import java.util.UUID;

public record UpdateProjectTemplateCommand(
        UUID id,
        String name,
        String description,
        String category,
        String visibility
) {}
