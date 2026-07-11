package com.company.scopery.modules.project.task.application.command;

import java.util.UUID;

public record CancelTaskCommand(
        UUID id,
        UUID projectId
) {}
