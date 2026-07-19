package com.company.scopery.modules.clientportal.uat.domain.model;
import com.company.scopery.modules.clientportal.uat.domain.enums.ClientUatAssignmentStatus;
import java.time.Instant; import java.util.UUID;
public record ClientUatAssignment(UUID id, UUID projectId, UUID testCaseId, UUID testRunId, UUID portalAccountId, ClientUatAssignmentStatus status, String notes, int version, Instant createdAt, Instant updatedAt) {
    public static ClientUatAssignment create(UUID projectId, UUID testCaseId, UUID testRunId, UUID portalAccountId, String notes) {
        Instant now = Instant.now();
        return new ClientUatAssignment(UUID.randomUUID(), projectId, testCaseId, testRunId, portalAccountId, ClientUatAssignmentStatus.ASSIGNED, notes, 0, now, now);
    }
}
