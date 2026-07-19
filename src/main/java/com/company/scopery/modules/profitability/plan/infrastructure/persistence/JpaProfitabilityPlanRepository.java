package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlan;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanRepository;
import com.company.scopery.modules.profitability.plan.infrastructure.mapper.ProfitabilityPlanPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitabilityPlanRepository implements ProfitabilityPlanRepository {
    private final SpringDataProfitabilityPlanJpaRepository springData;
    private final ProfitabilityPlanPersistenceMapper mapper;

    public JpaProfitabilityPlanRepository(SpringDataProfitabilityPlanJpaRepository springData,
                                          ProfitabilityPlanPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitabilityPlan save(ProfitabilityPlan plan) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(plan)));
    }

    @Override
    public Optional<ProfitabilityPlan> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProfitabilityPlan> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<ProfitabilityPlan> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
