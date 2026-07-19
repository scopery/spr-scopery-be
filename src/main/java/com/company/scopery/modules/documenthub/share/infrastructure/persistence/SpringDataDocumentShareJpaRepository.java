package com.company.scopery.modules.documenthub.share.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDocumentShareJpaRepository extends JpaRepository<DocumentShareJpaEntity, UUID> {
    Optional<DocumentShareJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentShareJpaEntity> findByProjectIdAndDocumentIdOrderByCreatedAtDesc(UUID projectId, UUID documentId);
}
