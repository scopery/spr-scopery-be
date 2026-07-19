package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOption;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.infrastructure.mapper.DecisionOptionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDecisionOptionRepository implements DecisionOptionRepository {
    private final SpringDataDecisionOptionJpaRepository springData; private final DecisionOptionPersistenceMapper mapper;
    public JpaDecisionOptionRepository(SpringDataDecisionOptionJpaRepository springData, DecisionOptionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DecisionOption save(DecisionOption o){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(o))); }
    @Override public Optional<DecisionOption> findById(UUID id){ return springData.findById(id).map(mapper::toDomain); }
    @Override public List<DecisionOption> findByDecisionId(UUID decisionId){ return springData.findByDecisionIdOrderByCreatedAtAsc(decisionId).stream().map(mapper::toDomain).toList(); }
    @Override public void deleteById(UUID id){ springData.deleteById(id); }
}
