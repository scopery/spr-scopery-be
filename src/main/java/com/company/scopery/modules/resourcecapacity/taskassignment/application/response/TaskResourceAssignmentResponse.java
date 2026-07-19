package com.company.scopery.modules.resourcecapacity.taskassignment.application.response;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.TaskResourceAssignment;
import java.math.BigDecimal; import java.util.UUID;
public record TaskResourceAssignmentResponse(UUID id, UUID projectId, UUID taskId, UUID resourceProfileId,
        String assignmentType, BigDecimal plannedEffortHours, String status) {
    public static TaskResourceAssignmentResponse from(TaskResourceAssignment a) {
        return new TaskResourceAssignmentResponse(a.id(), a.projectId(), a.taskId(), a.resourceProfileId(),
                a.assignmentType().name(), a.plannedEffortHours(), a.status().name());
    }
}
