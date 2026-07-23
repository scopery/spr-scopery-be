package com.company.scopery.modules.traceability.screenfield.application.action;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryScreenFieldAction {
    private final RegistryScreenFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryScreenFieldAction(RegistryScreenFieldRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(fieldId, workspaceId).orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(fieldId));
        repo.delete(fieldId, workspaceId);
    }
}
