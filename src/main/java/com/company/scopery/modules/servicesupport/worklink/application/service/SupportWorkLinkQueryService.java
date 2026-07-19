package com.company.scopery.modules.servicesupport.worklink.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.worklink.application.response.SupportWorkLinkResponse;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportWorkLinkQueryService {
    private final SupportWorkLinkRepository repo; private final SupportAuthorizationService auth;
    public SupportWorkLinkQueryService(SupportWorkLinkRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportWorkLinkResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportWorkLinkResponse::from).toList();
    }
}
