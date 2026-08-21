package com.company.scopery.modules.traceability.application.application.action;

import com.company.scopery.modules.traceability.application.application.command.UpdateRegistryApplicationCommand;
import com.company.scopery.modules.traceability.application.application.response.RegistryApplicationResponse;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplication;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryApplicationAction {

    private final RegistryApplicationRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public UpdateRegistryApplicationAction(RegistryApplicationRepository repo,
                                            TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public RegistryApplicationResponse execute(UpdateRegistryApplicationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryApplication app = repo.findByIdAndWorkspaceId(c.applicationId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(c.applicationId()));
        return RegistryApplicationResponse.from(repo.save(app.withUpdated(c.name().trim(), c.description())));
    }
}
