package com.company.scopery.modules.resourcecapacity.effortestimate.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface EffortEstimateRepository {
    EffortEstimate save(EffortEstimate e);
    Optional<EffortEstimate> findById(UUID id);
    List<EffortEstimate> findByProjectId(UUID projectId);
    List<EffortEstimate> findActiveByProjectId(UUID projectId);
}
