package com.company.scopery.modules.resourcecapacity.projectallocation.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectResourceAllocationCommand(
        UUID workspaceId,
        UUID projectId,
        UUID workspaceMemberId,
        BigDecimal allocationPercent,
        String allocationType,
        LocalDate startDate,
        LocalDate endDate,
        String notes
) {}
