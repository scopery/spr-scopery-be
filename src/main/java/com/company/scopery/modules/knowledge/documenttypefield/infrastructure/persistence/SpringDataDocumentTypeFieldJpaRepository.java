package com.company.scopery.modules.knowledge.documenttypefield.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentTypeFieldJpaRepository extends JpaRepository<DocumentTypeFieldJpaEntity, UUID> {

    List<DocumentTypeFieldJpaEntity> findByDocumentTypeIdOrderByDisplayOrderAsc(UUID documentTypeId);

    boolean existsByDocumentTypeIdAndFieldKey(UUID documentTypeId, String fieldKey);
}
