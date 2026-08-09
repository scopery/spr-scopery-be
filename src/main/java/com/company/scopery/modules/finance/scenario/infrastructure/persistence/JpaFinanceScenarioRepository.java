package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenario;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceScenarioRepository;
import com.company.scopery.modules.finance.scenario.infrastructure.mapper.FinanceScenarioPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFinanceScenarioRepository implements FinanceScenarioRepository {

    private final SpringDataFinanceScenarioJpaRepository springData;
    private final FinanceScenarioPersistenceMapper mapper;

    public JpaFinanceScenarioRepository(SpringDataFinanceScenarioJpaRepository springData,
                                        FinanceScenarioPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<FinanceScenario> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<FinanceScenario> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public Optional<FinanceScenario> findCurrentByProjectId(UUID projectId) {
        return springData.findByProjectIdAndCurrentFlagTrue(projectId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springData.existsByProjectIdAndCode(projectId, code);
    }

    @Override
    public List<FinanceScenario> findAllByProjectId(UUID projectId) {
        return springData.findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public FinanceScenario save(FinanceScenario scenario) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(scenario)));
    }

    @Override
    public void clearCurrentFlagForProject(UUID projectId) {
        springData.clearCurrentFlag(projectId);
    }
}
