package com.company.scopery.modules.knowledge.documenttype.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DocumentTypeRepository {
    DocumentType save(DocumentType documentType);
    Optional<DocumentType> findById(UUID id);
    boolean existsByCodeAndScopeSystem(DocumentTypeCode code);
    boolean existsByCodeAndWorkspaceId(DocumentTypeCode code, UUID workspaceId);
    Page<DocumentType> findAll(String keyword, UUID workspaceId, DocumentTypeScope documentScope,
                               DocumentTypeStatus status, boolean includeDeleted, Pageable pageable);
}
