package com.company.scopery.modules.project.task.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateTaskCommand(UUID projectId, List<CreateTaskCommand> items) {}
