package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OverAllocationResponse(
        UUID workspaceId,
        LocalDate fromDate,
        LocalDate toDate,
        List<OverAllocatedUser> overAllocatedUsers
) {}
