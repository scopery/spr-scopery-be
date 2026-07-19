package com.company.scopery.modules.project.templatetask.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProjectTemplateTaskRequest(
        @NotNull UUID templatePhaseId,
        UUID templateWbsNodeId,
        @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String title,
        String description,
        String defaultPriority,
        BigDecimal estimateHours,
        Integer dueOffsetDays,
        Integer startOffsetDays,
        @Size(max = 100) String defaultAssigneeRoleCode,
        UUID deliverableDocumentTypeId
) {}
