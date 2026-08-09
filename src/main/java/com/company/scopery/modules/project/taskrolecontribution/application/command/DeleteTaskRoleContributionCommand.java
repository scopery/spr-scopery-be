package com.company.scopery.modules.project.taskrolecontribution.application.command;

import java.util.UUID;

public record DeleteTaskRoleContributionCommand(UUID id, UUID taskId, UUID projectId) {}
