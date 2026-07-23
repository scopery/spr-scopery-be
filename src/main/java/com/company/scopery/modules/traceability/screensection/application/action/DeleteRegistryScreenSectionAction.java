package com.company.scopery.modules.traceability.screensection.application.action;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryScreenSectionAction {
    private final RegistryScreenSectionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryScreenSectionAction(RegistryScreenSectionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID sectionId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndWorkspaceId(sectionId, workspaceId).orElseThrow(() -> TraceabilityExceptions.screenSectionNotFound(sectionId));
        repo.delete(sectionId, workspaceId);
    }
}
