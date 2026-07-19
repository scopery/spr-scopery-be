package com.company.scopery.modules.project.templatetask.infrastructure.persistence;

import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templatetask.infrastructure.mapper.ProjectTemplateTaskPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectTemplateTaskRepository implements ProjectTemplateTaskRepository {

    private final SpringDataProjectTemplateTaskJpaRepository springDataRepository;
    private final ProjectTemplateTaskPersistenceMapper mapper;

    public JpaProjectTemplateTaskRepository(SpringDataProjectTemplateTaskJpaRepository springDataRepository,
                                            ProjectTemplateTaskPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectTemplateTask save(ProjectTemplateTask task) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(task)));
    }

    @Override
    public Optional<ProjectTemplateTask> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
        springDataRepository.flush();
    }

    @Override
    public List<ProjectTemplateTask> findByTemplateVersionId(UUID templateVersionId) {
        return springDataRepository.findByTemplateVersionIdOrderByTitleAsc(templateVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByTemplatePhaseId(UUID templatePhaseId) {
        return springDataRepository.existsByTemplatePhaseId(templatePhaseId);
    }

    @Override
    public boolean existsByTemplateWbsNodeId(UUID templateWbsNodeId) {
        return springDataRepository.existsByTemplateWbsNodeId(templateWbsNodeId);
    }
}
