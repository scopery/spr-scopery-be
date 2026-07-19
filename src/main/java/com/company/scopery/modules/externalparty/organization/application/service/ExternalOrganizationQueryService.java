package com.company.scopery.modules.externalparty.organization.application.service;
import com.company.scopery.modules.externalparty.organization.application.response.ExternalOrganizationResponse;
import com.company.scopery.modules.externalparty.organization.domain.model.ExternalOrganizationRepository;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExternalOrganizationQueryService {
    private final ExternalOrganizationRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    public ExternalOrganizationQueryService(ExternalOrganizationRepository repo, ExternalPartyAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ExternalOrganizationResponse> list(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ExternalOrganizationResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public ExternalOrganizationResponse get(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(id, workspaceId).map(ExternalOrganizationResponse::from)
                .orElseThrow(() -> ExternalPartyExceptions.organizationNotFound(id));
    }
}
