package com.company.scopery.modules.documenthub.document.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataDocumentJpaRepository extends JpaRepository<DocumentJpaEntity, UUID> {
    Optional<DocumentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
