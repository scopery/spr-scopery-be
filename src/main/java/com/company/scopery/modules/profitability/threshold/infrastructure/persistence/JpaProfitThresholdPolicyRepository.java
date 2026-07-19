package com.company.scopery.modules.profitability.threshold.infrastructure.persistence;
import com.company.scopery.modules.profitability.threshold.domain.model.*;
import com.company.scopery.modules.profitability.threshold.infrastructure.mapper.ProfitThresholdPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaProfitThresholdPolicyRepository implements ProfitThresholdPolicyRepository {
    private final SpringDataProfitThresholdPolicyJpaRepository springData; private final ProfitThresholdPolicyPersistenceMapper mapper;
    public JpaProfitThresholdPolicyRepository(SpringDataProfitThresholdPolicyJpaRepository springData, ProfitThresholdPolicyPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public ProfitThresholdPolicy save(ProfitThresholdPolicy p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<ProfitThresholdPolicy> findByProjectId(UUID projectId) { return springData.findByProjectId(projectId).map(mapper::toDomain); }
}
