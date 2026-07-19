package com.company.scopery.modules.servicesupport.warranty.domain.model;

import java.util.List;
import java.util.UUID;

public interface WarrantyCoverageRepository {
    WarrantyCoverage save(WarrantyCoverage coverage);
    java.util.Optional<WarrantyCoverage> findById(UUID id);
    List<WarrantyCoverage> findByWorkspaceId(UUID workspaceId);
    List<WarrantyCoverage> findByProjectId(UUID projectId);
}
