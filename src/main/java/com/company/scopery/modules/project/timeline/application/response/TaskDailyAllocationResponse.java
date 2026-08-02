package com.company.scopery.modules.project.timeline.application.response;

import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TaskDailyAllocationResponse(
        UUID id,
        UUID taskId,
        LocalDate workDate,
        int plannedMinutes,
        AllocationSource source
) {}
