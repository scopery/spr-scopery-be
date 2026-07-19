package com.company.scopery.modules.project.scheduling.schedulerun.infrastructure.persistence;

import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataScheduleRunJpaRepository extends JpaRepository<ScheduleRunJpaEntity, UUID> {
    List<ScheduleRunJpaEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);
}

@Repository
public class JpaScheduleRunRepository implements ScheduleRunRepository {
    private final SpringDataScheduleRunJpaRepository repository;
    public JpaScheduleRunRepository(SpringDataScheduleRunJpaRepository repository) { this.repository = repository; }

    public ScheduleRun save(ScheduleRun d) { return toDomain(repository.saveAndFlush(toEntity(d))); }
    public Optional<ScheduleRun> findById(UUID id) { return repository.findById(id).map(this::toDomain); }
    public List<ScheduleRun> findAllByProjectId(UUID projectId) {
        return repository.findAllByProjectIdOrderByCreatedAtDesc(projectId).stream().map(this::toDomain).toList();
    }
    public Optional<ScheduleRun> findCurrent(UUID projectId, UUID currentId) {
        return currentId == null ? Optional.empty() : findById(currentId).filter(r -> r.projectId().equals(projectId));
    }
    private ScheduleRun toDomain(ScheduleRunJpaEntity e) {
        return new ScheduleRun(e.getId(), e.getProjectId(), e.getWorkspaceId(), ScheduleRunStatus.valueOf(e.getStatus()),
                e.getAlgorithmVersion(), e.getPlanningStartDate(), e.getPlanningEndDate(), e.getInputSummaryJson(),
                e.getResultSummaryJson(), e.getErrorCode(), e.getErrorMessage(), e.getStartedAt(), e.getCompletedAt(),
                e.getActorUserId(), e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    private ScheduleRunJpaEntity toEntity(ScheduleRun d) {
        ScheduleRunJpaEntity e = new ScheduleRunJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setStatus(d.status().name());
        e.setAlgorithmVersion(d.algorithmVersion()); e.setPlanningStartDate(d.planningStartDate()); e.setPlanningEndDate(d.planningEndDate());
        e.setInputSummaryJson(d.inputSummaryJson()); e.setResultSummaryJson(d.resultSummaryJson()); e.setErrorCode(d.errorCode());
        e.setErrorMessage(d.errorMessage()); e.setStartedAt(d.startedAt()); e.setCompletedAt(d.completedAt());
        e.setActorUserId(d.actorUserId()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
