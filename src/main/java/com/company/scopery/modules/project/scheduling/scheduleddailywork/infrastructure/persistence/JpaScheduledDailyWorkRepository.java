package com.company.scopery.modules.project.scheduling.scheduleddailywork.infrastructure.persistence;

import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;

interface SpringDataScheduledDailyWorkJpaRepository extends JpaRepository<ScheduledDailyWorkJpaEntity, UUID> {
    List<ScheduledDailyWorkJpaEntity> findAllByScheduleRunId(UUID runId);
    List<ScheduledDailyWorkJpaEntity> findAllByScheduleRunIdAndWorkDateBetween(UUID runId, LocalDate from, LocalDate to);
}
@Repository
public class JpaScheduledDailyWorkRepository implements ScheduledDailyWorkRepository {
    private final SpringDataScheduledDailyWorkJpaRepository repository;
    public JpaScheduledDailyWorkRepository(SpringDataScheduledDailyWorkJpaRepository repository){this.repository=repository;}
    public List<ScheduledDailyWork> saveAll(List<ScheduledDailyWork> v){return repository.saveAllAndFlush(v.stream().map(this::entity).toList()).stream().map(this::domain).toList();}
    public List<ScheduledDailyWork> findAllByScheduleRunId(UUID r){return repository.findAllByScheduleRunId(r).stream().map(this::domain).toList();}
    public List<ScheduledDailyWork> findAllByScheduleRunIdAndDateRange(UUID r,LocalDate f,LocalDate t){return repository.findAllByScheduleRunIdAndWorkDateBetween(r,f,t).stream().map(this::domain).toList();}
    private ScheduledDailyWork domain(ScheduledDailyWorkJpaEntity e){return new ScheduledDailyWork(e.getId(),e.getScheduleRunId(),e.getTaskScheduleId(),e.getProjectId(),e.getTaskId(),e.getWorkspaceMemberId(),e.getUserId(),e.getWorkDate(),e.getPlannedHours(),e.getCapacityHours(),e.getRemainingCapacityAfter(),e.getCreatedAt(),e.getUpdatedAt());}
    private ScheduledDailyWorkJpaEntity entity(ScheduledDailyWork d){ScheduledDailyWorkJpaEntity e=new ScheduledDailyWorkJpaEntity();e.setId(d.id());e.setScheduleRunId(d.scheduleRunId());e.setTaskScheduleId(d.taskScheduleId());e.setProjectId(d.projectId());e.setTaskId(d.taskId());e.setWorkspaceMemberId(d.workspaceMemberId());e.setUserId(d.userId());e.setWorkDate(d.workDate());e.setPlannedHours(d.plannedHours());e.setCapacityHours(d.capacityHours());e.setRemainingCapacityAfter(d.remainingCapacityAfter());if(d.createdAt()!=null)e.setCreatedAt(d.createdAt());return e;}
}
