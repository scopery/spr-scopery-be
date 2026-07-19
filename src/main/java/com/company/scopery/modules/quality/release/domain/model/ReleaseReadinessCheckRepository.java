package com.company.scopery.modules.quality.release.domain.model;
import java.util.*;
public interface ReleaseReadinessCheckRepository {
    ReleaseReadinessCheck save(ReleaseReadinessCheck e);
    List<ReleaseReadinessCheck> findByReleasePackageId(UUID releasePackageId);
    void deleteByReleasePackageId(UUID releasePackageId);
}
