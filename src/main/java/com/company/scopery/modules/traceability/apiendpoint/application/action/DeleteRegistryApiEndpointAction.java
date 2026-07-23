package com.company.scopery.modules.traceability.apiendpoint.application.action;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DeleteRegistryApiEndpointAction {
    private final RegistryApiEndpointRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public DeleteRegistryApiEndpointAction(RegistryApiEndpointRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public void execute(UUID workspaceId, UUID applicationId, UUID endpointId) {
        authorization.requireWorkspaceCreate(workspaceId);
        repo.findByIdAndApplicationId(endpointId, applicationId).orElseThrow(() -> TraceabilityExceptions.apiEndpointNotFound(endpointId));
        repo.delete(endpointId, applicationId);
    }
}
