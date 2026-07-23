package com.company.scopery.modules.traceability.appcomponent.application.action;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryAppComponentAction {
    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryAppComponentAction(RegistryAppComponentRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID appComponentId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(appComponentId, workspaceId).orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(appComponentId));
        repo.delete(appComponentId, workspaceId);
    }
}
