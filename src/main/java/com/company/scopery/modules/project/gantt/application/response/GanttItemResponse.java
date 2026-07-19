package com.company.scopery.modules.project.gantt.application.response;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record GanttItemResponse(
        String id,
        String itemType,
        String sourceEntityType,
        UUID sourceEntityId,
        String parentItemId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String scheduleStatus,
        UUID assigneeUserId,
        UUID phaseId,
        UUID wbsNodeId,
        Integer sortOrder,
        boolean zeroDuration,
        Map<String, Object> metadata
) {}
