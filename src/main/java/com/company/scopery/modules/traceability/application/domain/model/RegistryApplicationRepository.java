package com.company.scopery.modules.traceability.application.domain.model;
import java.util.*;
public interface RegistryApplicationRepository {
    RegistryApplication save(RegistryApplication entity);
    Optional<RegistryApplication> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryApplication> findByWorkspaceId(UUID workspaceId);
}
