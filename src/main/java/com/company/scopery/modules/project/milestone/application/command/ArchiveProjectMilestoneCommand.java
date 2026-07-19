package com.company.scopery.modules.project.milestone.application.command;

import java.util.UUID;

public record ArchiveProjectMilestoneCommand(UUID projectId, UUID milestoneId) {}
