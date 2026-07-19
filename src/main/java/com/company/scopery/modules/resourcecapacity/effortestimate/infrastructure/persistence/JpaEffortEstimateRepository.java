package com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.*;
import com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.mapper.EffortEstimatePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaEffortEstimateRepository implements EffortEstimateRepository {
    private final SpringDataEffortEstimateJpaRepository spring; private final EffortEstimatePersistenceMapper mapper;
    public JpaEffortEstimateRepository(SpringDataEffortEstimateJpaRepository spring, EffortEstimatePersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public EffortEstimate save(EffortEstimate e) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<EffortEstimate> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<EffortEstimate> findByProjectId(UUID projectId) { return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public List<EffortEstimate> findActiveByProjectId(UUID projectId) {
        return spring.findByProjectIdAndStatus(projectId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
}
