package com.company.scopery.modules.project.milestone.infrastructure.persistence;

import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.milestone.infrastructure.mapper.ProjectMilestonePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectMilestoneRepository implements ProjectMilestoneRepository {

    private final SpringDataProjectMilestoneJpaRepository springDataRepository;
    private final ProjectMilestonePersistenceMapper mapper;

    public JpaProjectMilestoneRepository(SpringDataProjectMilestoneJpaRepository springDataRepository,
                                         ProjectMilestonePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectMilestone save(ProjectMilestone milestone) {
        ProjectMilestoneJpaEntity saved = springDataRepository.saveAndFlush(mapper.toJpaEntity(milestone));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProjectMilestone> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProjectMilestone> findAllByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springDataRepository.existsByProjectIdAndCode(projectId, code);
    }
}
