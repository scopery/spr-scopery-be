package com.company.scopery.modules.externalparty.address.application.service;

import com.company.scopery.modules.externalparty.address.application.response.ExternalPartyAddressResponse;
import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddressRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalPartyAddressQueryService {
    private final ExternalPartyAddressRepository repo;
    private final ExternalPartyAuthorizationService authorization;

    public ExternalPartyAddressQueryService(ExternalPartyAddressRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ExternalPartyAddressResponse> listByOrganization(UUID workspaceId, UUID organizationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByOrganizationId(workspaceId, organizationId).stream().map(ExternalPartyAddressResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ExternalPartyAddressResponse> listByContact(UUID workspaceId, UUID contactId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByContactId(workspaceId, contactId).stream().map(ExternalPartyAddressResponse::from).toList();
    }
}
