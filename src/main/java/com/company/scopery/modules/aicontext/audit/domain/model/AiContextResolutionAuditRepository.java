package com.company.scopery.modules.aicontext.audit.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AiContextResolutionAuditRepository {

    AiContextResolutionAudit save(AiContextResolutionAudit audit);

    Page<AiContextResolutionAudit> findByDocumentId(UUID documentId, Pageable pageable);
}
