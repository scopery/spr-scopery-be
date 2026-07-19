package com.company.scopery.modules.resourcecapacity.calculation.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectAllocationSummaryQuery(
        UUID projectId,
        LocalDate fromDate,
        LocalDate toDate
) {}
