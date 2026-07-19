package com.company.scopery.modules.project.templatetask.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProjectTemplateTaskCommand(
        UUID templateId,
        UUID versionId,
        UUID templatePhaseId,
        UUID templateWbsNodeId,
        String code,
        String title,
        String description,
        String defaultPriority,
        BigDecimal estimateHours,
        Integer dueOffsetDays,
        Integer startOffsetDays,
        String defaultAssigneeRoleCode,
        UUID deliverableDocumentTypeId
) {}
