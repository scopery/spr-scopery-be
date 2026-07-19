package com.company.scopery.modules.documenthub.attachment.application.service;

import com.company.scopery.modules.documenthub.attachment.application.response.DocumentAttachmentResponse;
import com.company.scopery.modules.documenthub.attachment.domain.model.DocumentAttachmentRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AttachmentQueryService {

    private final DocumentAttachmentRepository attachmentRepo;
    private final DocumentHubAuthorizationService authorization;

    public AttachmentQueryService(DocumentAttachmentRepository attachmentRepo,
                                   DocumentHubAuthorizationService authorization) {
        this.attachmentRepo = attachmentRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<DocumentAttachmentResponse> listByDocument(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return attachmentRepo.findByDocumentId(documentId).stream()
                .map(DocumentAttachmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentAttachmentResponse get(UUID projectId, UUID documentId, UUID attachmentId) {
        authorization.requireView(projectId);
        return attachmentRepo.findByIdAndDocumentId(attachmentId, documentId)
                .map(DocumentAttachmentResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.attachmentNotFound(attachmentId));
    }
}
