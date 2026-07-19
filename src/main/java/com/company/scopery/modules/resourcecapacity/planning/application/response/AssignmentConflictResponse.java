package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.util.UUID;
public record AssignmentConflictResponse(UUID id, UUID projectId, String conflictType, String severity, String status, String description) {}
