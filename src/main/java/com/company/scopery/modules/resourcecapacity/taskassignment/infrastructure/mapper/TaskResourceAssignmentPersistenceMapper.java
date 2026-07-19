package com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.enums.*;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.TaskResourceAssignment;
import com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.persistence.TaskResourceAssignmentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TaskResourceAssignmentPersistenceMapper {
    public TaskResourceAssignmentJpaEntity toJpaEntity(TaskResourceAssignment d) {
        TaskResourceAssignmentJpaEntity e = new TaskResourceAssignmentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setTaskId(d.taskId());
        e.setResourceProfileId(d.resourceProfileId()); e.setAssignmentType(d.assignmentType().name());
        e.setPlannedEffortHours(d.plannedEffortHours()); e.setStartDate(d.startDate()); e.setEndDate(d.endDate());
        e.setStatus(d.status().name()); e.setNotes(d.notes()); e.setRemovedAt(d.removedAt()); e.setRemovedBy(d.removedBy());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public TaskResourceAssignment toDomain(TaskResourceAssignmentJpaEntity e) {
        return new TaskResourceAssignment(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getTaskId(), e.getResourceProfileId(),
                TaskAssignmentType.valueOf(e.getAssignmentType()), e.getPlannedEffortHours(), e.getStartDate(), e.getEndDate(),
                TaskAssignmentStatus.valueOf(e.getStatus()), e.getNotes(), e.getRemovedAt(), e.getRemovedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
