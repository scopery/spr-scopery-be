package com.company.scopery.modules.resourcecapacity.threshold.domain.model;
import java.util.Optional; import java.util.UUID;
public interface UtilizationThresholdPolicyRepository {
    UtilizationThresholdPolicy save(UtilizationThresholdPolicy p);
    Optional<UtilizationThresholdPolicy> findByWorkspaceIdAndProjectIdIsNull(UUID workspaceId);
    Optional<UtilizationThresholdPolicy> findByProjectId(UUID projectId);
}
