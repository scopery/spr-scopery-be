package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WorkspaceOverviewResponse(
        UUID workspaceId,
        LocalDate fromDate,
        LocalDate toDate,
        List<UserCapacitySummary> users,
        BigDecimal totalFocusedHours,
        int overAllocatedUserCount
) {}
