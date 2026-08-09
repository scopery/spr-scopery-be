package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;
import com.company.scopery.modules.finance.scenario.domain.model.CustomCostRepository;
import com.company.scopery.modules.finance.scenario.infrastructure.mapper.CustomCostPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCustomCostRepository implements CustomCostRepository {

    private final SpringDataCustomCostJpaRepository springData;
    private final CustomCostPersistenceMapper mapper;

    public JpaCustomCostRepository(SpringDataCustomCostJpaRepository springData,
                                   CustomCostPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public List<CustomCost> findAllByScenarioId(UUID scenarioId) {
        return springData.findAllByFinanceScenarioId(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CustomCost> findActiveByScenarioId(UUID scenarioId) {
        return springData.findActiveByFinanceScenarioId(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<CustomCost> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public CustomCost save(CustomCost cost) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(cost)));
    }
}
