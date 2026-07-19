package com.company.scopery.modules.quality.releaseitem.domain.model;
import java.util.*;
public interface ReleaseItemRepository {
    ReleaseItem save(ReleaseItem entity);
    Optional<ReleaseItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<ReleaseItem> findByProjectIdAndReleasePackageId(UUID projectId, UUID releasePackageId);
}
