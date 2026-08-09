package com.company.scopery.modules.profitability.thresholdpolicy.infrastructure.persistence;

import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicy;
import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicyRepository;
import com.company.scopery.modules.profitability.thresholdpolicy.infrastructure.mapper.ProfitThresholdPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitThresholdPolicyRepository implements ProfitThresholdPolicyRepository {

    private final SpringDataProfitThresholdPolicyJpaRepository springData;
    private final ProfitThresholdPolicyPersistenceMapper mapper;

    public JpaProfitThresholdPolicyRepository(SpringDataProfitThresholdPolicyJpaRepository springData,
                                              ProfitThresholdPolicyPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProfitThresholdPolicy> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).map(mapper::toDomain);
    }

    @Override
    public ProfitThresholdPolicy save(ProfitThresholdPolicy policy) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(policy)));
    }
}
