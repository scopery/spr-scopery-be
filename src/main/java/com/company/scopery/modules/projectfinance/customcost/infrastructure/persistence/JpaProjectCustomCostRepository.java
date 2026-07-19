package com.company.scopery.modules.projectfinance.customcost.infrastructure.persistence;

import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostStatus;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;
import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCostRepository;
import com.company.scopery.modules.projectfinance.customcost.infrastructure.mapper.ProjectCustomCostPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectCustomCostRepository implements ProjectCustomCostRepository {
    private final SpringDataProjectCustomCostJpaRepository springData;
    private final ProjectCustomCostPersistenceMapper mapper;

    public JpaProjectCustomCostRepository(SpringDataProjectCustomCostJpaRepository springData,
                                          ProjectCustomCostPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProjectCustomCost save(ProjectCustomCost cost) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(cost)));
    }

    @Override
    public Optional<ProjectCustomCost> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProjectCustomCost> findByIdAndScenarioId(UUID id, UUID scenarioId) {
        return springData.findByIdAndFinanceScenarioId(id, scenarioId).map(mapper::toDomain);
    }

    @Override
    public List<ProjectCustomCost> findByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioIdOrderByCreatedAtDesc(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectCustomCost> findActiveByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioIdAndStatus(scenarioId, CustomCostStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }
}
