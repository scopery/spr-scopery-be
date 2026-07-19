package com.company.scopery.modules.quality.release.domain.model;
import java.util.*;
public interface ReleasePackageRepository {
    ReleasePackage save(ReleasePackage e);
    Optional<ReleasePackage> findByIdAndProjectId(UUID id, UUID projectId);
    List<ReleasePackage> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
