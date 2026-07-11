package com.company.scopery.modules.knowledge.documenttype.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;

import java.util.Optional;
import java.util.UUID;

public interface DocumentTypeRepository {
    DocumentType save(DocumentType documentType);
    Optional<DocumentType> findById(UUID id);
    boolean existsByCodeAndScopeSystem(DocumentTypeCode code);
    boolean existsByCodeAndWorkspaceId(DocumentTypeCode code, UUID workspaceId);
    PageResult<DocumentType> findAll(String keyword, UUID workspaceId, DocumentTypeScope documentScope,
                                      DocumentTypeStatus status, boolean includeDeleted, PageQuery pageQuery);
}
