package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentTemplateVersionJpaRepository
        extends JpaRepository<DocumentTemplateVersionJpaEntity, UUID> {
    Optional<DocumentTemplateVersionJpaEntity> findByIdAndTemplateId(UUID id, UUID templateId);
}
