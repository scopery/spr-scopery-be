package com.company.scopery.modules.servicesupport.supportcase.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportCaseQueryService {
    private final SupportCaseRepository repo; private final SupportAuthorizationService auth;
    public SupportCaseQueryService(SupportCaseRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportCaseResponse> listByWorkspace(UUID workspaceId, boolean portalView) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream()
                .filter(c -> !portalView || c.portalVisible())
                .map(SupportCaseResponse::from).toList();
    }
}
