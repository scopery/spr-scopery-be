package com.company.scopery.modules.workspace.member.application.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DailySummaryTaskItem(
        UUID id,
        String code,
        String title,
        UUID projectId,
        String projectName,
        UUID projectPhaseId,
        String phaseName,
        BigDecimal estimateHours,
        String status,
        Instant completedAt,
        Instant startedAt) {
}
