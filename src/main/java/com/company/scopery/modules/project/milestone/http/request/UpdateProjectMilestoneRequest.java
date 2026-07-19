package com.company.scopery.modules.project.milestone.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectMilestoneRequest(
        UUID projectPhaseId,
        UUID wbsNodeId,
        @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull LocalDate milestoneDate,
        Integer sortOrder
) {}
