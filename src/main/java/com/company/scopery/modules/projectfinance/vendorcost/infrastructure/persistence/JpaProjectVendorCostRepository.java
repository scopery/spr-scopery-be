package com.company.scopery.modules.projectfinance.vendorcost.infrastructure.persistence;

import com.company.scopery.modules.projectfinance.vendorcost.domain.enums.VendorCostStatus;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;
import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCostRepository;
import com.company.scopery.modules.projectfinance.vendorcost.infrastructure.mapper.ProjectVendorCostPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectVendorCostRepository implements ProjectVendorCostRepository {
    private final SpringDataProjectVendorCostJpaRepository springData;
    private final ProjectVendorCostPersistenceMapper mapper;

    public JpaProjectVendorCostRepository(SpringDataProjectVendorCostJpaRepository springData,
                                          ProjectVendorCostPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProjectVendorCost save(ProjectVendorCost cost) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(cost)));
    }

    @Override
    public Optional<ProjectVendorCost> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProjectVendorCost> findByIdAndScenarioId(UUID id, UUID scenarioId) {
        return springData.findByIdAndFinanceScenarioId(id, scenarioId).map(mapper::toDomain);
    }

    @Override
    public List<ProjectVendorCost> findByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioIdOrderByCreatedAtDesc(scenarioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectVendorCost> findActiveByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioIdAndStatus(scenarioId, VendorCostStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }
}
