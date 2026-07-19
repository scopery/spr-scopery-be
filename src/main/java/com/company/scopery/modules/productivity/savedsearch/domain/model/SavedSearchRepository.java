package com.company.scopery.modules.productivity.savedsearch.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SavedSearchRepository {
    SavedSearch save(SavedSearch s);
    Optional<SavedSearch> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<SavedSearch> findByWorkspaceId(UUID workspaceId);
}
