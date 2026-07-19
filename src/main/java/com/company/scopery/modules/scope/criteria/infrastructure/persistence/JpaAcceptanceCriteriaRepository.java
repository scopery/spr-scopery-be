package com.company.scopery.modules.scope.criteria.infrastructure.persistence;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.criteria.infrastructure.mapper.AcceptanceCriteriaPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaAcceptanceCriteriaRepository implements AcceptanceCriteriaRepository {
    private final SpringDataAcceptanceCriteriaJpaRepository springData; private final AcceptanceCriteriaPersistenceMapper mapper;
    public JpaAcceptanceCriteriaRepository(SpringDataAcceptanceCriteriaJpaRepository springData, AcceptanceCriteriaPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public AcceptanceCriteria save(AcceptanceCriteria c){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<AcceptanceCriteria> findById(UUID id){ return springData.findById(id).map(mapper::toDomain); }
    @Override public List<AcceptanceCriteria> findByDeliverableId(UUID deliverableId){ return springData.findByDeliverableIdOrderByCreatedAtAsc(deliverableId).stream().map(mapper::toDomain).toList(); }
}
