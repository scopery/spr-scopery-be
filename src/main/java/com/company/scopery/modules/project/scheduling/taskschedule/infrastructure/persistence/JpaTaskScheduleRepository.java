package com.company.scopery.modules.project.scheduling.taskschedule.infrastructure.persistence;

import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.*;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

interface SpringDataTaskScheduleJpaRepository extends JpaRepository<TaskScheduleJpaEntity, UUID> {
    Optional<TaskScheduleJpaEntity> findByScheduleRunIdAndTaskId(UUID runId, UUID taskId);
    List<TaskScheduleJpaEntity> findAllByScheduleRunId(UUID runId);
    List<TaskScheduleJpaEntity> findAllByProjectIdAndTaskIdOrderByCreatedAtDesc(UUID projectId, UUID taskId);
}
@Repository
public class JpaTaskScheduleRepository implements TaskScheduleRepository {
    private final SpringDataTaskScheduleJpaRepository repository;
    public JpaTaskScheduleRepository(SpringDataTaskScheduleJpaRepository repository) { this.repository=repository; }
    public List<TaskSchedule> saveAll(List<TaskSchedule> values) {
        return repository.saveAllAndFlush(values.stream().map(this::entity).toList()).stream().map(this::domain).toList();
    }
    public Optional<TaskSchedule> findByScheduleRunIdAndTaskId(UUID r, UUID t) { return repository.findByScheduleRunIdAndTaskId(r,t).map(this::domain); }
    public List<TaskSchedule> findAllByScheduleRunId(UUID r) { return repository.findAllByScheduleRunId(r).stream().map(this::domain).toList(); }
    public List<TaskSchedule> findHistory(UUID p, UUID t) { return repository.findAllByProjectIdAndTaskIdOrderByCreatedAtDesc(p,t).stream().map(this::domain).toList(); }
    private TaskSchedule domain(TaskScheduleJpaEntity e) {
        return new TaskSchedule(e.getId(),e.getScheduleRunId(),e.getProjectId(),e.getTaskId(),e.getAssigneeUserId(),
                e.getWorkspaceMemberId(),e.getEstimatedStartDate(),e.getEstimatedFinishDate(),e.getScheduledHours(),
                e.getUnscheduledHours(),e.getDueDate(),e.getDueDateCapacityGapHours(),
                TaskScheduleRiskStatus.valueOf(e.getRiskStatus()),TaskScheduleStatus.valueOf(e.getScheduleStatus()),
                e.getCreatedAt(),e.getUpdatedAt());
    }
    private TaskScheduleJpaEntity entity(TaskSchedule d) {
        TaskScheduleJpaEntity e=new TaskScheduleJpaEntity(); e.setId(d.id());e.setScheduleRunId(d.scheduleRunId());
        e.setProjectId(d.projectId());e.setTaskId(d.taskId());e.setAssigneeUserId(d.assigneeUserId());
        e.setWorkspaceMemberId(d.workspaceMemberId());e.setEstimatedStartDate(d.estimatedStartDate());
        e.setEstimatedFinishDate(d.estimatedFinishDate());e.setScheduledHours(d.scheduledHours());
        e.setUnscheduledHours(d.unscheduledHours());e.setDueDate(d.dueDate());e.setDueDateCapacityGapHours(d.dueDateCapacityGapHours());
        e.setRiskStatus(d.riskStatus().name());e.setScheduleStatus(d.scheduleStatus().name());
        if(d.createdAt()!=null)e.setCreatedAt(d.createdAt()); return e;
    }
}
