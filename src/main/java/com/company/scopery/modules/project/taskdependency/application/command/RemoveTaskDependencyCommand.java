package com.company.scopery.modules.project.taskdependency.application.command;

import java.util.UUID;

public record RemoveTaskDependencyCommand(
        UUID id,
        UUID projectId
) {}
