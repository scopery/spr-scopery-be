package com.company.scopery.modules.project.timeline.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ReplaceDailyAllocationsRequest(
        @NotNull @Valid List<DailyAllocationItemRequest> items
) {
    public record DailyAllocationItemRequest(
            @NotNull LocalDate workDate,
            @Min(0) int plannedMinutes
    ) {}
}
