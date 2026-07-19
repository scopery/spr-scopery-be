package com.company.scopery.modules.quality.defect.application.command;

import java.util.UUID;

public record ReopenDefectCommand(UUID projectId, UUID defectId, String reason) {}
