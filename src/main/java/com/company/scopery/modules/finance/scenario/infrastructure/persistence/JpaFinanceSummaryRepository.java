package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummary;
import com.company.scopery.modules.finance.scenario.domain.model.FinanceSummaryRepository;
import com.company.scopery.modules.finance.scenario.infrastructure.mapper.FinanceSummaryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFinanceSummaryRepository implements FinanceSummaryRepository {

    private final SpringDataFinanceSummaryJpaRepository springData;
    private final FinanceSummaryPersistenceMapper mapper;

    public JpaFinanceSummaryRepository(SpringDataFinanceSummaryJpaRepository springData,
                                       FinanceSummaryPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<FinanceSummary> findByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioId(scenarioId).map(mapper::toDomain);
    }

    @Override
    public FinanceSummary save(FinanceSummary summary) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(summary)));
    }
}
