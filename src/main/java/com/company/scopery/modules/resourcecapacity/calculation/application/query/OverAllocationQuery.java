package com.company.scopery.modules.resourcecapacity.calculation.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record OverAllocationQuery(
        UUID workspaceId,
        LocalDate fromDate,
        LocalDate toDate
) {}
