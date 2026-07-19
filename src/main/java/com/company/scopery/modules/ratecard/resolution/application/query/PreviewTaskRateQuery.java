package com.company.scopery.modules.ratecard.resolution.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record PreviewTaskRateQuery(
        UUID taskId,
        UUID workspaceId,
        UUID costRoleId,
        String costRoleCode,
        LocalDate targetDate,
        String currencyCode
) {}
