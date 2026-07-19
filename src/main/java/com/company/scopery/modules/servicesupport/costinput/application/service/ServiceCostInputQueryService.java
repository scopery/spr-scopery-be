package com.company.scopery.modules.servicesupport.costinput.application.service;
import com.company.scopery.modules.servicesupport.costinput.application.response.ServiceCostInputResponse;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInputRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class ServiceCostInputQueryService {
    private final ServiceCostInputRepository repo; private final SupportAuthorizationService auth;
    public ServiceCostInputQueryService(ServiceCostInputRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<ServiceCostInputResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ServiceCostInputResponse::from).toList();
    }
}
