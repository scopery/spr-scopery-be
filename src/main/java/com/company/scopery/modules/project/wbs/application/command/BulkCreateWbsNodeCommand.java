package com.company.scopery.modules.project.wbs.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateWbsNodeCommand(
        UUID projectId,
        List<CreateWbsNodeCommand> items
) {}
