package com.company.scopery.modules.traceability.screenaction.application.action;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenActionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryScreenActionAction {
    private final RegistryScreenActionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryScreenActionAction(RegistryScreenActionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID actionId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(actionId, workspaceId).orElseThrow(() -> TraceabilityExceptions.screenActionNotFound(actionId));
        repo.delete(actionId, workspaceId);
    }
}
