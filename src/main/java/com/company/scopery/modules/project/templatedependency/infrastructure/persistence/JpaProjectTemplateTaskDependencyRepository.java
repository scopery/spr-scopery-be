package com.company.scopery.modules.project.templatedependency.infrastructure.persistence;

import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatedependency.infrastructure.mapper.ProjectTemplateTaskDependencyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplateTaskDependencyRepository implements ProjectTemplateTaskDependencyRepository {

    private final SpringDataProjectTemplateTaskDependencyJpaRepository springDataRepository;
    private final ProjectTemplateTaskDependencyPersistenceMapper mapper;

    public JpaProjectTemplateTaskDependencyRepository(
            SpringDataProjectTemplateTaskDependencyJpaRepository springDataRepository,
            ProjectTemplateTaskDependencyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplateTaskDependency save(ProjectTemplateTaskDependency dependency) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(dependency)));
    }

    @Override
    public Optional<ProjectTemplateTaskDependency> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        springDataRepository.flush();
    }

    @Override
    public List<ProjectTemplateTaskDependency> findByTemplateVersionId(UUID templateVersionId) {
        return springDataRepository.findByTemplateVersionId(templateVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByPredecessorAndSuccessorAndType(
            UUID predecessorId, UUID successorId, TaskDependencyType type) {
        return springDataRepository.existsByPredecessorTemplateTaskIdAndSuccessorTemplateTaskIdAndDependencyType(
                predecessorId, successorId, type.name());
    }

    @Override
    public List<ProjectTemplateTaskDependency> findOutgoingDependencies(UUID predecessorTemplateTaskId) {
        return springDataRepository.findByPredecessorTemplateTaskId(predecessorTemplateTaskId)
                .stream().map(mapper::toDomain).toList();
    }
}
