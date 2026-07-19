package com.company.scopery.modules.project.milestone.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectMilestoneCommand(
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String code,
        String name,
        String description,
        LocalDate milestoneDate,
        Integer sortOrder
) {}
