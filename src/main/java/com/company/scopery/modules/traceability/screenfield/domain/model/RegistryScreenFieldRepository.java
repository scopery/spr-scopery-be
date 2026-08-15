package com.company.scopery.modules.traceability.screenfield.domain.model;
import java.util.*;
public interface RegistryScreenFieldRepository {
    RegistryScreenField save(RegistryScreenField entity);
    Optional<RegistryScreenField> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenField> findByScreenId(UUID screenId);
    List<RegistryScreenField> findByScreenIdAndComponentFieldIdIn(UUID screenId, List<UUID> componentFieldIds);
    void delete(UUID id, UUID workspaceId);
    void deleteAllByIds(List<UUID> ids);
}
