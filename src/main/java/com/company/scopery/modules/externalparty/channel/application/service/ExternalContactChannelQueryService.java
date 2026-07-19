package com.company.scopery.modules.externalparty.channel.application.service;

import com.company.scopery.modules.externalparty.channel.application.response.ExternalContactChannelResponse;
import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannelRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalContactChannelQueryService {
    private final ExternalContactChannelRepository repo;
    private final ExternalPartyAuthorizationService authorization;

    public ExternalContactChannelQueryService(ExternalContactChannelRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ExternalContactChannelResponse> listByContact(UUID workspaceId, UUID contactId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByContactId(workspaceId, contactId).stream().map(ExternalContactChannelResponse::from).toList();
    }
}
