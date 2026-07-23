package com.company.scopery.modules.traceability.screencomponent.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScreenComponentRepository {
    ScreenComponent save(ScreenComponent link);
    boolean existsByScreenIdAndComponentId(UUID screenId, UUID componentId);
    Optional<ScreenComponent> findByScreenIdAndComponentId(UUID screenId, UUID componentId);
    List<ScreenComponent> findByScreenId(UUID screenId);
    List<ScreenComponent> findByScreenIdIn(Collection<UUID> screenIds);
    List<ScreenComponent> findByComponentId(UUID componentId);
    void deleteByScreenIdAndComponentId(UUID screenId, UUID componentId);
}
