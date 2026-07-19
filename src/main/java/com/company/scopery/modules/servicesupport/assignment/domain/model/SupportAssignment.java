package com.company.scopery.modules.servicesupport.assignment.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SupportAssignment(UUID id, UUID workspaceId, UUID supportCaseId, String assignmentType,
        UUID assigneeUserId, UUID resourceProfileId, String status, Instant createdAt) {
    public static SupportAssignment assignUser(UUID workspaceId, UUID caseId, UUID userId) {
        return new SupportAssignment(UUID.randomUUID(), workspaceId, caseId, "USER", userId, null, "ACTIVE", Instant.now());
    }
    public static SupportAssignment assignResource(UUID workspaceId, UUID caseId, UUID resourceProfileId) {
        return new SupportAssignment(UUID.randomUUID(), workspaceId, caseId, "RESOURCE", null, resourceProfileId, "ACTIVE", Instant.now());
    }
    public SupportAssignment release() {
        return new SupportAssignment(id, workspaceId, supportCaseId, assignmentType, assigneeUserId, resourceProfileId, "RELEASED", createdAt);
    }
}
