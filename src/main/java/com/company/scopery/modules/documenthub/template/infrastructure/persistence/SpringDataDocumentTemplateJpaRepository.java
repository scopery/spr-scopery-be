package com.company.scopery.modules.documenthub.template.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDocumentTemplateJpaRepository extends JpaRepository<DocumentTemplateJpaEntity, UUID> {
    Optional<DocumentTemplateJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DocumentTemplateJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
