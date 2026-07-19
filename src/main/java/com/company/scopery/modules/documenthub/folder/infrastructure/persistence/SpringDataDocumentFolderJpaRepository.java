package com.company.scopery.modules.documenthub.folder.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDocumentFolderJpaRepository extends JpaRepository<DocumentFolderJpaEntity, UUID> {
    Optional<DocumentFolderJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentFolderJpaEntity> findByProjectIdOrderBySortOrderAscCreatedAtDesc(UUID projectId);
}
