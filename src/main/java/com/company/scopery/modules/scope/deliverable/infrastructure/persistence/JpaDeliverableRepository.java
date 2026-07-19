package com.company.scopery.modules.scope.deliverable.infrastructure.persistence;
import com.company.scopery.modules.scope.deliverable.domain.model.Deliverable;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.deliverable.infrastructure.mapper.DeliverablePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDeliverableRepository implements DeliverableRepository {
    private final SpringDataDeliverableJpaRepository springData; private final DeliverablePersistenceMapper mapper;
    public JpaDeliverableRepository(SpringDataDeliverableJpaRepository springData, DeliverablePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public Deliverable save(Deliverable d){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(d))); }
    @Override public Optional<Deliverable> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<Deliverable> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
}
