package com.company.scopery.modules.project.task.application.command;

import java.util.UUID;

public record ReopenTaskCommand(UUID id, UUID projectId) {}
