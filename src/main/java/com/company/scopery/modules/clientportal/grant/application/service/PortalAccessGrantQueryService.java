package com.company.scopery.modules.clientportal.grant.application.service;
import com.company.scopery.modules.clientportal.grant.application.response.ExternalProjectAccessGrantResponse;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrantRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PortalAccessGrantQueryService {
    private final ExternalProjectAccessGrantRepository repo;
    private final ClientPortalAuthorizationService authorization;
    public PortalAccessGrantQueryService(ExternalProjectAccessGrantRepository repo, ClientPortalAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ExternalProjectAccessGrantResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ExternalProjectAccessGrantResponse::from).toList();
    }
}
