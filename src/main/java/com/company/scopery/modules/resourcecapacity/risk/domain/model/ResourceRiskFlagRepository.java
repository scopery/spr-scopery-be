package com.company.scopery.modules.resourcecapacity.risk.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ResourceRiskFlagRepository {
    ResourceRiskFlag save(ResourceRiskFlag f);
    Optional<ResourceRiskFlag> findById(UUID id);
    List<ResourceRiskFlag> findByProjectId(UUID projectId);
}
