package com.company.scopery.modules.project.taskrolecontribution.infrastructure.persistence;

import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContribution;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContributionRepository;
import com.company.scopery.modules.project.taskrolecontribution.infrastructure.mapper.TaskRoleContributionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskRoleContributionRepository implements TaskRoleContributionRepository {

    private final SpringDataTaskRoleContributionJpaRepository springDataRepository;
    private final TaskRoleContributionPersistenceMapper mapper;

    public JpaTaskRoleContributionRepository(SpringDataTaskRoleContributionJpaRepository springDataRepository,
                                              TaskRoleContributionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskRoleContribution save(TaskRoleContribution contribution) {
        TaskRoleContributionJpaEntity entity = mapper.toJpaEntity(contribution);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<TaskRoleContribution> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TaskRoleContribution> findAllByTaskId(UUID taskId) {
        return springDataRepository.findAllByTaskId(taskId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskRoleContribution> findAllByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}
