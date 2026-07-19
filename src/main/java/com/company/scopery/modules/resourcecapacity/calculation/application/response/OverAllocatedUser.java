package com.company.scopery.modules.resourcecapacity.calculation.application.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OverAllocatedUser(
        UUID userId,
        UUID workspaceMemberId,
        BigDecimal totalAllocationPercent,
        List<UUID> allocationIds
) {}
