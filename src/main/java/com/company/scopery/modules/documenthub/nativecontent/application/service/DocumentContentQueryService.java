package com.company.scopery.modules.documenthub.nativecontent.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentRevisionResponse;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevisionRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentContentQueryService {

    private final DocumentContentRepository contentRepo;
    private final DocumentRevisionRepository revisionRepo;
    private final DocumentHubAuthorizationService authorization;

    public DocumentContentQueryService(DocumentContentRepository contentRepo,
                                        DocumentRevisionRepository revisionRepo,
                                        DocumentHubAuthorizationService authorization) {
        this.contentRepo = contentRepo;
        this.revisionRepo = revisionRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public DocumentContentResponse getContent(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return contentRepo.findByDocumentId(documentId)
                .map(DocumentContentResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.contentNotFound(documentId));
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentRevisionResponse> listRevisions(UUID projectId, UUID documentId, int page, int size) {
        authorization.requireView(projectId);
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(revisionRepo.findByDocumentId(documentId, pageable)
                .map(DocumentRevisionResponse::fromWithoutAst));
    }

    @Transactional(readOnly = true)
    public DocumentRevisionResponse getRevision(UUID projectId, UUID documentId, long revisionNo) {
        authorization.requireView(projectId);
        return revisionRepo.findByDocumentIdAndRevisionNo(documentId, revisionNo)
                .map(DocumentRevisionResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.contentNotFound(documentId));
    }
}
