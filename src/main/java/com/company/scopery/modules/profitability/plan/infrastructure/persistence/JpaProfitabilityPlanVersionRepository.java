package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersion;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersionRepository;
import com.company.scopery.modules.profitability.plan.infrastructure.mapper.ProfitabilityPlanVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitabilityPlanVersionRepository implements ProfitabilityPlanVersionRepository {
    private final SpringDataProfitabilityPlanVersionJpaRepository springData;
    private final ProfitabilityPlanVersionPersistenceMapper mapper;

    public JpaProfitabilityPlanVersionRepository(SpringDataProfitabilityPlanVersionJpaRepository springData,
                                                  ProfitabilityPlanVersionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitabilityPlanVersion save(ProfitabilityPlanVersion version) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(version)));
    }

    @Override
    public Optional<ProfitabilityPlanVersion> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProfitabilityPlanVersion> findByIdAndPlanId(UUID id, UUID planId) {
        return springData.findByIdAndProfitabilityPlanId(id, planId).map(mapper::toDomain);
    }

    @Override
    public List<ProfitabilityPlanVersion> findByPlanId(UUID planId) {
        return springData.findByProfitabilityPlanIdOrderByVersionNumberDesc(planId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countByPlanId(UUID planId) {
        return springData.countByProfitabilityPlanId(planId);
    }
}
