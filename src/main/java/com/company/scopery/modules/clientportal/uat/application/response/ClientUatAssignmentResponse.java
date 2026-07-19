package com.company.scopery.modules.clientportal.uat.application.response;
import com.company.scopery.modules.clientportal.uat.domain.model.ClientUatAssignment;
import java.time.Instant; import java.util.UUID;
public record ClientUatAssignmentResponse(UUID id, UUID projectId, UUID testCaseId, UUID portalAccountId, String status, Instant createdAt) {
    public static ClientUatAssignmentResponse from(ClientUatAssignment e) {
        return new ClientUatAssignmentResponse(e.id(), e.projectId(), e.testCaseId(), e.portalAccountId(), e.status().name(), e.createdAt());
    }
}
