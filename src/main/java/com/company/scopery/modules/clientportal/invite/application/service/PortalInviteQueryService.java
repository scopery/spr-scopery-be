package com.company.scopery.modules.clientportal.invite.application.service;
import com.company.scopery.modules.clientportal.invite.application.response.ExternalPortalInviteResponse;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInviteRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PortalInviteQueryService {
    private final ExternalPortalInviteRepository repo;
    private final ClientPortalAuthorizationService authorization;
    public PortalInviteQueryService(ExternalPortalInviteRepository repo, ClientPortalAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ExternalPortalInviteResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ExternalPortalInviteResponse::from).toList();
    }
}
