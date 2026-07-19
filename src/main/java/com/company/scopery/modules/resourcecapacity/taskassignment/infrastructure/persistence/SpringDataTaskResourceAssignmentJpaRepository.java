package com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataTaskResourceAssignmentJpaRepository extends JpaRepository<TaskResourceAssignmentJpaEntity, UUID> {
    List<TaskResourceAssignmentJpaEntity> findByTaskIdAndStatus(UUID taskId, String status);
    List<TaskResourceAssignmentJpaEntity> findByProjectId(UUID projectId);
    boolean existsByTaskIdAndResourceProfileIdAndAssignmentTypeAndStatus(UUID taskId, UUID resourceProfileId, String assignmentType, String status);
}
