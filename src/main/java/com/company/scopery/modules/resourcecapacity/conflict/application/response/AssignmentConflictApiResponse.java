package com.company.scopery.modules.resourcecapacity.conflict.application.response;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflict;
import java.util.UUID;
public record AssignmentConflictApiResponse(UUID id, UUID projectId, String conflictType, String severity, String status, String description) {
    public static AssignmentConflictApiResponse from(AssignmentConflict c) {
        return new AssignmentConflictApiResponse(c.id(), c.projectId(), c.conflictType(), c.severity(), c.status(), c.description());
    }
}
