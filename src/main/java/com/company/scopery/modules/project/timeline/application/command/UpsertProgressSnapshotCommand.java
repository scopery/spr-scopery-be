package com.company.scopery.modules.project.timeline.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpsertProgressSnapshotCommand(
        UUID projectId,
        UUID taskId,
        LocalDate snapshotDate,
        BigDecimal progressPercent,
        Integer timeSpentMinutes,
        String note
) {}
