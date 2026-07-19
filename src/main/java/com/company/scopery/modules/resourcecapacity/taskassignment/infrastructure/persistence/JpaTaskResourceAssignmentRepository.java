package com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.*;
import com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.mapper.TaskResourceAssignmentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaTaskResourceAssignmentRepository implements TaskResourceAssignmentRepository {
    private final SpringDataTaskResourceAssignmentJpaRepository spring; private final TaskResourceAssignmentPersistenceMapper mapper;
    public JpaTaskResourceAssignmentRepository(SpringDataTaskResourceAssignmentJpaRepository spring, TaskResourceAssignmentPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public TaskResourceAssignment save(TaskResourceAssignment a) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(a))); }
    @Override public Optional<TaskResourceAssignment> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<TaskResourceAssignment> findActiveByTaskId(UUID taskId) {
        return spring.findByTaskIdAndStatus(taskId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
    @Override public List<TaskResourceAssignment> findByProjectId(UUID projectId) {
        return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsActive(UUID taskId, UUID resourceProfileId, String assignmentType) {
        return spring.existsByTaskIdAndResourceProfileIdAndAssignmentTypeAndStatus(taskId, resourceProfileId, assignmentType, "ACTIVE");
    }
}
