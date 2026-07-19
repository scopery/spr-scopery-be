package com.company.scopery.modules.quality.releaseitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataReleaseItemJpaRepository extends JpaRepository<ReleaseItemJpaEntity, UUID> {
    Optional<ReleaseItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ReleaseItemJpaEntity> findByProjectIdAndReleasePackageIdOrderByCreatedAtDesc(UUID projectId, UUID releasePackageId);
}
