package com.company.scopery.modules.project.timeline.infrastructure.persistence;

import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocation;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocationRepository;
import com.company.scopery.modules.project.timeline.infrastructure.mapper.TaskDailyAllocationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaTaskDailyAllocationRepository implements TaskDailyAllocationRepository {

    private final SpringDataTaskDailyAllocationJpaRepository springDataRepository;
    private final TaskDailyAllocationPersistenceMapper mapper;

    public JpaTaskDailyAllocationRepository(
            SpringDataTaskDailyAllocationJpaRepository springDataRepository,
            TaskDailyAllocationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskDailyAllocation save(TaskDailyAllocation allocation) {
        TaskDailyAllocationJpaEntity saved = springDataRepository.saveAndFlush(mapper.toJpaEntity(allocation));
        return mapper.toDomain(saved);
    }

    @Override
    public List<TaskDailyAllocation> saveAll(List<TaskDailyAllocation> allocations) {
        List<TaskDailyAllocationJpaEntity> entities = allocations.stream().map(mapper::toJpaEntity).toList();
        return springDataRepository.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskDailyAllocation> findByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskDailyAllocation> findByTaskId(UUID taskId) {
        return springDataRepository.findAllByTaskId(taskId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteManualByTaskId(UUID taskId) {
        springDataRepository.deleteByTaskIdAndSource(taskId, AllocationSource.MANUAL.name());
    }
}
