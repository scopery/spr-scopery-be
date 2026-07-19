package com.company.scopery.modules.projectfinance.scenario.infrastructure.persistence;

import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenarioRepository;
import com.company.scopery.modules.projectfinance.scenario.infrastructure.mapper.ProjectFinanceScenarioPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectFinanceScenarioRepository implements ProjectFinanceScenarioRepository {

    private final SpringDataProjectFinanceScenarioJpaRepository springData;
    private final ProjectFinanceScenarioPersistenceMapper mapper;

    public JpaProjectFinanceScenarioRepository(SpringDataProjectFinanceScenarioJpaRepository springData,
                                               ProjectFinanceScenarioPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProjectFinanceScenario save(ProjectFinanceScenario scenario) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(scenario)));
    }

    @Override
    public Optional<ProjectFinanceScenario> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProjectFinanceScenario> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<ProjectFinanceScenario> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProjectFinanceScenario> findCurrentByProjectId(UUID projectId) {
        return springData.findByProjectIdAndCurrentFlagTrue(projectId).map(mapper::toDomain);
    }

    @Override
    public List<ProjectFinanceScenario> findCurrentFlagged(UUID projectId) {
        return springData.findByProjectIdAndCurrentFlagTrueOrderByCreatedAtDesc(projectId)
                .stream().map(mapper::toDomain).toList();
    }
}
