package com.company.scopery.modules.profitability.summary.infrastructure.persistence;
import com.company.scopery.modules.profitability.summary.domain.model.*;
import com.company.scopery.modules.profitability.summary.infrastructure.mapper.ProfitabilitySummaryPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProjectProfitabilitySummaryRepository implements ProjectProfitabilitySummaryRepository {
    private final SpringDataProjectProfitabilitySummaryJpaRepository springData;
    private final ProfitabilitySummaryPersistenceMapper mapper;
    public JpaProjectProfitabilitySummaryRepository(SpringDataProjectProfitabilitySummaryJpaRepository springData,
                                                    ProfitabilitySummaryPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ProjectProfitabilitySummary save(ProjectProfitabilitySummary s) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s)));
    }
    @Override public Optional<ProjectProfitabilitySummary> findByProjectIdAndCurrency(UUID projectId, String currency) {
        return springData.findByProjectIdAndCurrency(projectId, currency).map(mapper::toDomain);
    }
}
