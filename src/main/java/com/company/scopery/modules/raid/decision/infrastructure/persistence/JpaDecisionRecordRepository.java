package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.decision.infrastructure.mapper.DecisionRecordPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDecisionRecordRepository implements DecisionRecordRepository {
    private final SpringDataDecisionRecordJpaRepository springData; private final DecisionRecordPersistenceMapper mapper;
    public JpaDecisionRecordRepository(SpringDataDecisionRecordJpaRepository springData, DecisionRecordPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DecisionRecord save(DecisionRecord d){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(d))); }
    @Override public Optional<DecisionRecord> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<DecisionRecord> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
}
