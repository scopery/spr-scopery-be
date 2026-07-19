package com.company.scopery.modules.resourcecapacity.calculation.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record UserAvailabilityQuery(
        UUID workspaceId,
        UUID userId,
        LocalDate fromDate,
        LocalDate toDate
) {}
