package com.company.scopery.modules.quality.release.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataReleasePackageJpaRepository extends JpaRepository<ReleasePackageJpaEntity, UUID> {
    Optional<ReleasePackageJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ReleasePackageJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
