package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpact;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpactRepository;
import com.company.scopery.modules.raid.decision.infrastructure.mapper.DecisionImpactPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDecisionImpactRepository implements DecisionImpactRepository {
    private final SpringDataDecisionImpactJpaRepository springData; private final DecisionImpactPersistenceMapper mapper;
    public JpaDecisionImpactRepository(SpringDataDecisionImpactJpaRepository springData, DecisionImpactPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DecisionImpact save(DecisionImpact impact){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(impact))); }
    @Override public Optional<DecisionImpact> findByDecisionId(UUID decisionId){ return springData.findByDecisionId(decisionId).map(mapper::toDomain); }
}
