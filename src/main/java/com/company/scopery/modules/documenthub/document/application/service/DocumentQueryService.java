package com.company.scopery.modules.documenthub.document.application.service;
import com.company.scopery.modules.documenthub.document.application.response.DocumentDetailResponse;
import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.application.response.DocumentSearchHitResponse;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.application.DocumentPayloadMaskingService;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentQueryService {
    private final DocumentRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentPayloadMaskingService masking;

    public DocumentQueryService(DocumentRepository repo, DocumentHubAuthorizationService authorization,
                                DocumentPayloadMaskingService masking) {
        this.repo = repo;
        this.authorization = authorization;
        this.masking = masking;
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(DocumentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DocumentResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(id));
    }

    @Transactional(readOnly = true)
    public DocumentDetailResponse getWithMaskedFields(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        var doc = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> DocumentHubExceptions.documentNotFound(id));
        return DocumentDetailResponse.from(doc, buildMaskedPayload(doc));
    }

    /**
     * Lightweight project search with Phase 38 masking on description snippets.
     * Not a full-text engine rewrite — filters in-memory over project documents.
     */
    @Transactional(readOnly = true)
    public List<DocumentSearchHitResponse> search(UUID projectId, String q) {
        authorization.requireView(projectId);
        String needle = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return repo.findByProjectId(projectId).stream()
                .filter(d -> needle.isBlank()
                        || contains(d.code(), needle)
                        || contains(d.title(), needle)
                        || contains(d.description(), needle))
                .map(d -> {
                    Map<String, Object> masked = buildMaskedPayload(d);
                    return DocumentSearchHitResponse.from(d, String.valueOf(masked.getOrDefault("description", "")));
                })
                .toList();
    }

    private Map<String, Object> buildMaskedPayload(Document doc) {
        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("title", doc.title());
        raw.put("description", doc.description());
        raw.put("classification", doc.classification());
        // Only real document fields — no demo PII placeholders.
        return masking.maskDocumentPayload(doc.workspaceId(), raw);
    }

    private static boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }
}
