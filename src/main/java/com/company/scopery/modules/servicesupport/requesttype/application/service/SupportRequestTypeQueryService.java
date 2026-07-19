package com.company.scopery.modules.servicesupport.requesttype.application.service;
import com.company.scopery.modules.servicesupport.requesttype.application.response.SupportRequestTypeResponse;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestTypeRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportRequestTypeQueryService {
    private final SupportRequestTypeRepository repo; private final SupportAuthorizationService auth;
    public SupportRequestTypeQueryService(SupportRequestTypeRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportRequestTypeResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportRequestTypeResponse::from).toList();
    }
}
