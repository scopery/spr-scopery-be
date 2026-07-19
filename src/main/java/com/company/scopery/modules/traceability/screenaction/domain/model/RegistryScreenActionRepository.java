package com.company.scopery.modules.traceability.screenaction.domain.model;
import java.util.*;
public interface RegistryScreenActionRepository {
    RegistryScreenAction save(RegistryScreenAction entity);
    Optional<RegistryScreenAction> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenAction> findByScreenId(UUID screenId);
}
