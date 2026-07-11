package com.company.scopery.modules.project.task.application.command;

import java.util.UUID;

public record CompleteTaskCommand(
        UUID id,
        UUID projectId
) {}
