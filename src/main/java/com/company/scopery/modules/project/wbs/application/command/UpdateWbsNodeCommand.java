package com.company.scopery.modules.project.wbs.application.command;

import java.util.UUID;

public record UpdateWbsNodeCommand(
        UUID id,
        UUID projectId,
        String title,
        String description,
        String nodeType
) {}
