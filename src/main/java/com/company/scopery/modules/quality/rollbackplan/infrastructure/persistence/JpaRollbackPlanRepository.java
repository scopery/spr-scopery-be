package com.company.scopery.modules.quality.rollbackplan.infrastructure.persistence;
import com.company.scopery.modules.quality.rollbackplan.domain.model.*;
import com.company.scopery.modules.quality.rollbackplan.infrastructure.mapper.RollbackPlanPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRollbackPlanRepository implements RollbackPlanRepository {
    private final SpringDataRollbackPlanJpaRepository springData;
    private final RollbackPlanPersistenceMapper mapper;
    public JpaRollbackPlanRepository(SpringDataRollbackPlanJpaRepository springData, RollbackPlanPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RollbackPlan save(RollbackPlan e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RollbackPlan> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<RollbackPlan> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
