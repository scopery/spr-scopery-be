package com.company.scopery.modules.quality.release.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataReleaseReadinessCheckJpaRepository extends JpaRepository<ReleaseReadinessCheckJpaEntity, UUID> {
    List<ReleaseReadinessCheckJpaEntity> findByReleasePackageIdOrderByCreatedAtAsc(UUID releasePackageId);
    void deleteByReleasePackageId(UUID releasePackageId);
}
