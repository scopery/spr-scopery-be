package com.company.scopery.modules.traceability.screen.application.action;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryScreenAction {
    private final RegistryScreenRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryScreenAction(RegistryScreenRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID applicationId, UUID screenId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndApplicationId(screenId, applicationId).orElseThrow(() -> TraceabilityExceptions.screenNotFound(screenId));
        repo.delete(screenId, applicationId);
    }
}
