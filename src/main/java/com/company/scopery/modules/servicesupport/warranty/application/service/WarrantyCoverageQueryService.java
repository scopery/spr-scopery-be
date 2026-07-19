package com.company.scopery.modules.servicesupport.warranty.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.warranty.application.response.WarrantyCoverageResponse;
import com.company.scopery.modules.servicesupport.warranty.domain.model.WarrantyCoverageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class WarrantyCoverageQueryService {
    private final WarrantyCoverageRepository repo; private final SupportAuthorizationService auth;
    public WarrantyCoverageQueryService(WarrantyCoverageRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<WarrantyCoverageResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(WarrantyCoverageResponse::from).toList();
    }
}
