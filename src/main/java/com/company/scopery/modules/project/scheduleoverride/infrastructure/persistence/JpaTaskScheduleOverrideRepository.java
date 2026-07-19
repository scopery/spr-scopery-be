package com.company.scopery.modules.project.scheduleoverride.infrastructure.persistence;

import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideStatus;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.scheduleoverride.infrastructure.mapper.TaskScheduleOverridePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskScheduleOverrideRepository implements TaskScheduleOverrideRepository {

    private final SpringDataTaskScheduleOverrideJpaRepository springDataRepository;
    private final TaskScheduleOverridePersistenceMapper mapper;

    public JpaTaskScheduleOverrideRepository(SpringDataTaskScheduleOverrideJpaRepository springDataRepository,
                                             TaskScheduleOverridePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskScheduleOverride save(TaskScheduleOverride override) {
        TaskScheduleOverrideJpaEntity saved = springDataRepository.saveAndFlush(mapper.toJpaEntity(override));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TaskScheduleOverride> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TaskScheduleOverride> findActiveByTaskId(UUID taskId) {
        return springDataRepository.findByTaskIdAndStatus(taskId, ScheduleOverrideStatus.ACTIVE.name())
                .map(mapper::toDomain);
    }

    @Override
    public List<TaskScheduleOverride> findActiveByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectIdAndStatus(projectId, ScheduleOverrideStatus.ACTIVE.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
