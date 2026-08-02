package com.company.scopery.modules.project.timeline.application.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskProgressSnapshotResponse(
        UUID id,
        UUID projectId,
        UUID taskId,
        LocalDate snapshotDate,
        BigDecimal progressPercent,
        Integer timeSpentMinutes,
        String note,
        UUID recordedBy,
        Instant recordedAt
) {}
