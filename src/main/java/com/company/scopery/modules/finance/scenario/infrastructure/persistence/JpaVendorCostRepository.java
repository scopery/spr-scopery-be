package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;
import com.company.scopery.modules.finance.scenario.domain.model.VendorCostRepository;
import com.company.scopery.modules.finance.scenario.infrastructure.mapper.VendorCostPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaVendorCostRepository implements VendorCostRepository {

    private final SpringDataVendorCostJpaRepository springData;
    private final VendorCostPersistenceMapper mapper;

    public JpaVendorCostRepository(SpringDataVendorCostJpaRepository springData,
                                   VendorCostPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public List<VendorCost> findAllByScenarioId(UUID scenarioId) {
        return springData.findAllByFinanceScenarioId(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<VendorCost> findActiveByScenarioId(UUID scenarioId) {
        return springData.findActiveByFinanceScenarioId(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<VendorCost> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public VendorCost save(VendorCost cost) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(cost)));
    }
}
