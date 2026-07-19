package com.company.scopery.modules.configuration.statusset.domain.model;
import java.util.*; import java.util.UUID;
public interface StatusSetRepository {
    StatusSet save(StatusSet s);
    Optional<StatusSet> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<StatusSet> findByWorkspaceId(UUID workspaceId);
}
