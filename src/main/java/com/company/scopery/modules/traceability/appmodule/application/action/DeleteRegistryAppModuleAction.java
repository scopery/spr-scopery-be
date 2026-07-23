package com.company.scopery.modules.traceability.appmodule.application.action;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryAppModuleAction {
    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryAppModuleAction(RegistryAppModuleRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID appModuleId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(appModuleId, workspaceId).orElseThrow(() -> TraceabilityExceptions.appModuleNotFound(appModuleId));
        repo.delete(appModuleId, workspaceId);
    }
}
