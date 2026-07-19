package com.company.scopery.modules.quality.rollbackplan.domain.model;
import java.util.*;
public interface RollbackPlanRepository {
    RollbackPlan save(RollbackPlan entity);
    Optional<RollbackPlan> findByIdAndProjectId(UUID id, UUID projectId);
    List<RollbackPlan> findByProjectId(UUID projectId);
}
