package com.company.scopery.modules.projectbaseline.baseline.infrastructure.persistence;

import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.baseline.infrastructure.mapper.ProjectBaselinePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectBaselineRepository implements ProjectBaselineRepository {
    private final SpringDataProjectBaselineJpaRepository springData;
    private final ProjectBaselinePersistenceMapper mapper;

    public JpaProjectBaselineRepository(SpringDataProjectBaselineJpaRepository springData,
                                        ProjectBaselinePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override public ProjectBaseline save(ProjectBaseline baseline) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(baseline)));
    }
    @Override public Optional<ProjectBaseline> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
    @Override public Optional<ProjectBaseline> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ProjectBaseline> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByBaselineNumberDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<ProjectBaseline> findCurrentByProjectId(UUID projectId) {
        return springData.findByProjectIdAndCurrentFlagTrue(projectId).map(mapper::toDomain);
    }
    @Override public List<ProjectBaseline> findCurrentFlagged(UUID projectId) {
        return springData.findByProjectIdAndCurrentFlagTrueOrderByBaselineNumberDesc(projectId)
                .stream().map(mapper::toDomain).toList();
    }
    @Override public int nextBaselineNumber(UUID projectId) {
        return springData.findMaxBaselineNumber(projectId) + 1;
    }
}
