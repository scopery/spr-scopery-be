package com.company.scopery.modules.externalparty.documentlink.application.service;

import com.company.scopery.modules.externalparty.documentlink.application.response.ExternalPartyDocumentLinkResponse;
import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLinkRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalPartyDocumentLinkQueryService {
    private final ExternalPartyDocumentLinkRepository repo;
    private final ExternalPartyAuthorizationService authorization;

    public ExternalPartyDocumentLinkQueryService(ExternalPartyDocumentLinkRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ExternalPartyDocumentLinkResponse> listByOrganization(UUID workspaceId, UUID organizationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByOrganizationId(workspaceId, organizationId).stream().map(ExternalPartyDocumentLinkResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ExternalPartyDocumentLinkResponse> listByContact(UUID workspaceId, UUID contactId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByContactId(workspaceId, contactId).stream().map(ExternalPartyDocumentLinkResponse::from).toList();
    }
}
