package com.company.scopery.modules.resourcecapacity.taskassignment.domain.model;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.enums.*;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record TaskResourceAssignment(UUID id, UUID workspaceId, UUID projectId, UUID taskId, UUID resourceProfileId,
        TaskAssignmentType assignmentType, BigDecimal plannedEffortHours, LocalDate startDate, LocalDate endDate,
        TaskAssignmentStatus status, String notes, Instant removedAt, UUID removedBy, int version, Instant createdAt, Instant updatedAt) {
    public static TaskResourceAssignment create(UUID workspaceId, UUID projectId, UUID taskId, UUID resourceProfileId,
                                                TaskAssignmentType type, BigDecimal hours) {
        Instant now = Instant.now();
        return new TaskResourceAssignment(UUID.randomUUID(), workspaceId, projectId, taskId, resourceProfileId, type, hours,
                null, null, TaskAssignmentStatus.ACTIVE, null, null, null, 0, now, now);
    }
    public TaskResourceAssignment remove(UUID actorId) {
        if (status != TaskAssignmentStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE can be removed");
        return new TaskResourceAssignment(id, workspaceId, projectId, taskId, resourceProfileId, assignmentType, plannedEffortHours,
                startDate, endDate, TaskAssignmentStatus.REMOVED, notes, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
