package com.company.scopery.modules.project.gantt.application.command;

import java.util.UUID;

public record ClearGanttTaskOverrideCommand(UUID projectId, UUID taskId, boolean recalculate) {}
