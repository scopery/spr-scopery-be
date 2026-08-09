package com.company.scopery.modules.profitability.summary.infrastructure.persistence;

import com.company.scopery.modules.profitability.summary.domain.model.ProfitabilitySummary;
import com.company.scopery.modules.profitability.summary.domain.model.ProfitabilitySummaryRepository;
import com.company.scopery.modules.profitability.summary.infrastructure.mapper.ProfitabilitySummaryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitabilitySummaryRepository implements ProfitabilitySummaryRepository {

    private final SpringDataProfitabilitySummaryJpaRepository springData;
    private final ProfitabilitySummaryPersistenceMapper mapper;

    public JpaProfitabilitySummaryRepository(SpringDataProfitabilitySummaryJpaRepository springData,
                                             ProfitabilitySummaryPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProfitabilitySummary> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).map(mapper::toDomain);
    }

    @Override
    public ProfitabilitySummary save(ProfitabilitySummary summary) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(summary)));
    }
}
