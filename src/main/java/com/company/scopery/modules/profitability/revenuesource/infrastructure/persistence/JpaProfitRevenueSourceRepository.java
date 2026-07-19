package com.company.scopery.modules.profitability.revenuesource.infrastructure.persistence;

import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSource;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSourceRepository;
import com.company.scopery.modules.profitability.revenuesource.infrastructure.mapper.ProfitRevenueSourcePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitRevenueSourceRepository implements ProfitRevenueSourceRepository {
    private final SpringDataProfitRevenueSourceJpaRepository springData;
    private final ProfitRevenueSourcePersistenceMapper mapper;

    public JpaProfitRevenueSourceRepository(SpringDataProfitRevenueSourceJpaRepository springData,
                                            ProfitRevenueSourcePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitRevenueSource save(ProfitRevenueSource source) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(source)));
    }

    @Override
    public List<ProfitRevenueSource> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProfitRevenueSource> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
}
