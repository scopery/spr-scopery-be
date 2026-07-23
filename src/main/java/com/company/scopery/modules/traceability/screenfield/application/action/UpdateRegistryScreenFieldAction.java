package com.company.scopery.modules.traceability.screenfield.application.action;
import com.company.scopery.modules.traceability.screenfield.application.command.UpdateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.response.RegistryScreenFieldResponse;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryScreenFieldAction {
    private final RegistryScreenFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryScreenFieldAction(RegistryScreenFieldRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenFieldResponse execute(UpdateRegistryScreenFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var field = repo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(c.fieldId()));
        return RegistryScreenFieldResponse.from(repo.save(field.withUpdated(c.label(), c.fieldType(), c.description(), c.required(), c.displayOrder())));
    }
}
