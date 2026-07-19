package com.company.scopery.modules.profitability.riskflag.infrastructure.persistence;

import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlag;
import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlagRepository;
import com.company.scopery.modules.profitability.riskflag.infrastructure.mapper.ProfitRiskFlagPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitRiskFlagRepository implements ProfitRiskFlagRepository {
    private final SpringDataProfitRiskFlagJpaRepository springData;
    private final ProfitRiskFlagPersistenceMapper mapper;

    public JpaProfitRiskFlagRepository(SpringDataProfitRiskFlagJpaRepository springData,
                                       ProfitRiskFlagPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public ProfitRiskFlag save(ProfitRiskFlag riskFlag) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(riskFlag)));
    }

    @Override
    public Optional<ProfitRiskFlag> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProfitRiskFlag> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProfitRiskFlag> findByProjectIdAndStatus(UUID projectId, String status) {
        return springData.findByProjectIdAndStatusOrderByCreatedAtDesc(projectId, status).stream().map(mapper::toDomain).toList();
    }
}
