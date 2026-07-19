package com.company.scopery.modules.profitability.variance.infrastructure.persistence;

import com.company.scopery.modules.profitability.variance.domain.model.ProfitVariance;
import com.company.scopery.modules.profitability.variance.domain.model.ProfitVarianceRepository;
import com.company.scopery.modules.profitability.variance.infrastructure.mapper.ProfitVariancePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitVarianceRepository implements ProfitVarianceRepository {
    private final SpringDataProfitVarianceJpaRepository springData;
    private final ProfitVariancePersistenceMapper mapper;

    public JpaProfitVarianceRepository(SpringDataProfitVarianceJpaRepository springData,
                                       ProfitVariancePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitVariance save(ProfitVariance variance) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(variance)));
    }

    @Override
    public Optional<ProfitVariance> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProfitVariance> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
