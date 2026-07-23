package com.company.scopery.modules.traceability.screen.application.action;
import com.company.scopery.modules.traceability.screen.application.command.UpdateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryScreenAction {
    private final RegistryScreenRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryScreenAction(RegistryScreenRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenResponse execute(UpdateRegistryScreenCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var screen = repo.findByIdAndApplicationId(c.screenId(), c.applicationId())
                .orElseThrow(() -> TraceabilityExceptions.screenNotFound(c.screenId()));
        return RegistryScreenResponse.from(repo.save(screen.withUpdated(c.name(), c.routePath())));
    }
}
