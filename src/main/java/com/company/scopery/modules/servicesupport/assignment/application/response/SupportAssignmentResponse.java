package com.company.scopery.modules.servicesupport.assignment.application.response;
import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignment;
import java.time.Instant; import java.util.UUID;
public record SupportAssignmentResponse(UUID id, UUID workspaceId, UUID supportCaseId, String assignmentType,
        UUID assigneeUserId, UUID resourceProfileId, String status, Instant createdAt) {
    public static SupportAssignmentResponse from(SupportAssignment a) {
        return new SupportAssignmentResponse(a.id(), a.workspaceId(), a.supportCaseId(), a.assignmentType(),
                a.assigneeUserId(), a.resourceProfileId(), a.status(), a.createdAt());
    }
}
