package com.company.scopery.modules.trust.anonymization.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AnonymizationPlanRepository {
    AnonymizationPlan save(AnonymizationPlan plan);
    Optional<AnonymizationPlan> findById(UUID id);
    List<AnonymizationPlan> findByWorkspaceId(UUID workspaceId);
}
