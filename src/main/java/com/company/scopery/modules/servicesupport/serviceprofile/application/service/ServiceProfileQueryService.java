package com.company.scopery.modules.servicesupport.serviceprofile.application.service;
import com.company.scopery.modules.servicesupport.serviceprofile.application.response.ServiceProfileResponse;
import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfileRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class ServiceProfileQueryService {
    private final ServiceProfileRepository repo; private final SupportAuthorizationService auth;
    public ServiceProfileQueryService(ServiceProfileRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<ServiceProfileResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ServiceProfileResponse::from).toList();
    }
}
