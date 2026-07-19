package com.company.scopery.modules.projectfinance.summary.infrastructure.persistence;

import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummaryRepository;
import com.company.scopery.modules.projectfinance.summary.infrastructure.mapper.ProjectFinanceSummaryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectFinanceSummaryRepository implements ProjectFinanceSummaryRepository {
    private final SpringDataProjectFinanceSummaryJpaRepository springData;
    private final ProjectFinanceSummaryPersistenceMapper mapper;

    public JpaProjectFinanceSummaryRepository(SpringDataProjectFinanceSummaryJpaRepository springData,
                                              ProjectFinanceSummaryPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProjectFinanceSummary save(ProjectFinanceSummary summary) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(summary)));
    }

    @Override
    public Optional<ProjectFinanceSummary> findByScenarioId(UUID scenarioId) {
        return springData.findByFinanceScenarioId(scenarioId).map(mapper::toDomain);
    }

    @Override
    public void deleteByScenarioId(UUID scenarioId) {
        springData.deleteByFinanceScenarioId(scenarioId);
    }
}
