package com.company.scopery.modules.traceability.screenaction.application.action;
import com.company.scopery.modules.traceability.screenaction.application.command.UpdateRegistryScreenActionCommand;
import com.company.scopery.modules.traceability.screenaction.application.response.RegistryScreenActionResponse;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenActionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryScreenActionAction {
    private final RegistryScreenActionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryScreenActionAction(RegistryScreenActionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenActionResponse execute(UpdateRegistryScreenActionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var action = repo.findByIdAndWorkspaceId(c.actionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenActionNotFound(c.actionId()));
        return RegistryScreenActionResponse.from(repo.save(action.withUpdated(c.name(), c.actionType(), c.description(), c.displayOrder())));
    }
}
