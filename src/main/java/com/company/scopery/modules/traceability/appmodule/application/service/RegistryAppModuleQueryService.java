package com.company.scopery.modules.traceability.appmodule.application.service;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryAppModuleQueryService {
    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryAppModuleQueryService(RegistryAppModuleRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryAppModuleResponse> list(UUID applicationId) {
        return repo.findByApplicationId(applicationId).stream().map(RegistryAppModuleResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryAppModuleResponse get(UUID workspaceId, UUID appModuleId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(appModuleId, workspaceId).map(RegistryAppModuleResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.appModuleNotFound(appModuleId));
    }
}
