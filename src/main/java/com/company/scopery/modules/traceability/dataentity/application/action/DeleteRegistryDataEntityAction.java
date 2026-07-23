package com.company.scopery.modules.traceability.dataentity.application.action;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryDataEntityAction {
    private final RegistryDataEntityRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryDataEntityAction(RegistryDataEntityRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID dataEntityId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(dataEntityId, workspaceId).orElseThrow(() -> TraceabilityExceptions.dataEntityNotFound(dataEntityId));
        repo.delete(dataEntityId, workspaceId);
    }
}
