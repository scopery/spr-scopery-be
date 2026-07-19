package com.company.scopery.modules.profitability.profile.domain.model;
import java.util.Optional; import java.util.UUID;
public interface ProjectProfitabilityProfileRepository {
    ProjectProfitabilityProfile save(ProjectProfitabilityProfile p);
    Optional<ProjectProfitabilityProfile> findByProjectId(UUID projectId);
    boolean existsByProjectId(UUID projectId);
}
