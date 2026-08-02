package com.company.scopery.modules.project.timeline.application.command;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReplaceDailyAllocationsCommand(
        UUID projectId,
        UUID taskId,
        List<DailyAllocationItem> items
) {
    public record DailyAllocationItem(LocalDate workDate, int plannedMinutes) {}
}
