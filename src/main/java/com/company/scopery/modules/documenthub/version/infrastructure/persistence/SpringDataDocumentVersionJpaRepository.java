package com.company.scopery.modules.documenthub.version.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataDocumentVersionJpaRepository extends JpaRepository<DocumentVersionJpaEntity, UUID> {
    Optional<DocumentVersionJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentVersionJpaEntity> findByProjectIdAndDocumentIdOrderByVersionNumberDesc(UUID projectId, UUID documentId);
    @Query("select coalesce(max(e.versionNumber), 0) from DocumentVersionJpaEntity e where e.documentId = :documentId")
    int maxVersionNumber(@Param("documentId") UUID documentId);
}
