package com.company.scopery.modules.externalparty.preference.application.service;

import com.company.scopery.modules.externalparty.preference.application.response.CommunicationPreferenceResponse;
import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreferenceRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CommunicationPreferenceQueryService {
    private final CommunicationPreferenceRepository repo;
    private final ExternalPartyAuthorizationService authorization;

    public CommunicationPreferenceQueryService(CommunicationPreferenceRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public CommunicationPreferenceResponse getByOrganization(UUID workspaceId, UUID organizationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByOrganizationId(workspaceId, organizationId)
                .map(CommunicationPreferenceResponse::from)
                .orElse(CommunicationPreferenceResponse.empty(workspaceId, organizationId, null));
    }

    @Transactional(readOnly = true)
    public CommunicationPreferenceResponse getByContact(UUID workspaceId, UUID contactId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByContactId(workspaceId, contactId)
                .map(CommunicationPreferenceResponse::from)
                .orElse(CommunicationPreferenceResponse.empty(workspaceId, null, contactId));
    }
}
