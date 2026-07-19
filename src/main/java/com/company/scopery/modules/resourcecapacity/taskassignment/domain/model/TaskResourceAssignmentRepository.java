package com.company.scopery.modules.resourcecapacity.taskassignment.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface TaskResourceAssignmentRepository {
    TaskResourceAssignment save(TaskResourceAssignment a);
    Optional<TaskResourceAssignment> findById(UUID id);
    List<TaskResourceAssignment> findActiveByTaskId(UUID taskId);
    List<TaskResourceAssignment> findByProjectId(UUID projectId);
    boolean existsActive(UUID taskId, UUID resourceProfileId, String assignmentType);
}
