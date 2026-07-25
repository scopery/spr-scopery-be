package com.company.scopery.modules.project.projectphase.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateProjectPhaseCommand(
        UUID projectId,
        List<CreateProjectPhaseCommand> items
) {}
