package com.company.scopery.modules.quality.defect.application.command;

import java.util.UUID;

public record AssignDefectCommand(UUID projectId, UUID defectId, UUID assignedToUserId) {}
