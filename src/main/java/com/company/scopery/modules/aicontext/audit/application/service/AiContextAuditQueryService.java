package com.company.scopery.modules.aicontext.audit.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAudit;
import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAuditRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiContextAuditQueryService {

    private final AiContextResolutionAuditRepository auditRepo;

    public AiContextAuditQueryService(AiContextResolutionAuditRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Transactional(readOnly = true)
    public PageResponse<AiContextAuditEntry> listByDocument(UUID documentId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("resolvedAt").descending());
        return PageResponse.from(auditRepo.findByDocumentId(documentId, pageable).map(AiContextAuditEntry::from));
    }

    public record AiContextAuditEntry(UUID id, UUID policyId, UUID documentId, UUID actorId,
                                       Integer tokenCount, Integer blockCount, String status, String errorMessage) {
        public static AiContextAuditEntry from(AiContextResolutionAudit a) {
            return new AiContextAuditEntry(a.id(), a.policyId(), a.documentId(), a.actorId(),
                    a.tokenCount(), a.blockCount(), a.status(), a.errorMessage());
        }
    }
}
