package com.company.scopery.modules.traceability.screensection.domain.model;
import java.util.*;
public interface RegistryScreenSectionRepository {
    RegistryScreenSection save(RegistryScreenSection entity);
    Optional<RegistryScreenSection> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenSection> findByScreenId(UUID screenId);
    void delete(UUID id, UUID workspaceId);
}
