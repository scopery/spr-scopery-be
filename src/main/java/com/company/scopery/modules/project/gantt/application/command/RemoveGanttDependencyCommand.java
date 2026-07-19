package com.company.scopery.modules.project.gantt.application.command;

import java.util.UUID;

public record RemoveGanttDependencyCommand(UUID projectId, UUID dependencyId, boolean recalculate) {}
