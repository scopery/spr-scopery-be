package com.company.scopery.modules.quality.defect.application.command;

import java.util.UUID;

public record CloseDefectCommand(
        UUID projectId,
        UUID defectId,
        String resolutionType,
        String resolutionNote) {}
