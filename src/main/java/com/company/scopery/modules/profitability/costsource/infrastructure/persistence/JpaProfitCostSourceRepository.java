package com.company.scopery.modules.profitability.costsource.infrastructure.persistence;

import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSource;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSourceRepository;
import com.company.scopery.modules.profitability.costsource.infrastructure.mapper.ProfitCostSourcePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitCostSourceRepository implements ProfitCostSourceRepository {
    private final SpringDataProfitCostSourceJpaRepository springData;
    private final ProfitCostSourcePersistenceMapper mapper;

    public JpaProfitCostSourceRepository(SpringDataProfitCostSourceJpaRepository springData,
                                         ProfitCostSourcePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitCostSource save(ProfitCostSource source) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(source)));
    }

    @Override
    public List<ProfitCostSource> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProfitCostSource> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
}
