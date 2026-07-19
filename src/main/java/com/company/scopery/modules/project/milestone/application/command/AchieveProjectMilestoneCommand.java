package com.company.scopery.modules.project.milestone.application.command;

import java.util.UUID;

public record AchieveProjectMilestoneCommand(UUID projectId, UUID milestoneId) {}
