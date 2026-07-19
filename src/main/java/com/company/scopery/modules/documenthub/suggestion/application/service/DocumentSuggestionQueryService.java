package com.company.scopery.modules.documenthub.suggestion.application.service;

import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.suggestion.application.response.SuggestionResponse;
import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentSuggestionQueryService {

    private final DocumentSuggestionRepository suggestionRepo;
    private final DocumentHubAuthorizationService authorization;

    public DocumentSuggestionQueryService(DocumentSuggestionRepository suggestionRepo,
                                           DocumentHubAuthorizationService authorization) {
        this.suggestionRepo = suggestionRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> listByDocument(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return suggestionRepo.findByDocumentId(documentId).stream()
                .map(SuggestionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SuggestionResponse get(UUID projectId, UUID documentId, UUID suggestionId) {
        authorization.requireView(projectId);
        return suggestionRepo.findByIdAndDocumentId(suggestionId, documentId)
                .map(SuggestionResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.suggestionNotFound(suggestionId));
    }
}
