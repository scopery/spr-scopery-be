package com.company.scopery.modules.resourcecapacity.calculation.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record WorkspaceOverviewQuery(
        UUID workspaceId,
        LocalDate fromDate,
        LocalDate toDate
) {}
