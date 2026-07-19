package com.company.scopery.modules.traceability.tracelink.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataTraceLinkJpaRepository extends JpaRepository<TraceLinkJpaEntity, UUID> {
    Optional<TraceLinkJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<TraceLinkJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
