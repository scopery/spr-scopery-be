package com.company.scopery.modules.traceability.specdocrevision.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateRegistrySpecDocRevisionCommand(
        UUID workspaceId,
        UUID revisionId,
        String revisionNo,
        String targetSheetName,
        String details,
        String personInCharge,
        String color,
        LocalDate changedAt,
        int displayOrder) {}
