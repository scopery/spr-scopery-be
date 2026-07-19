package com.company.scopery.modules.traceability.screen.application.action;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryScreenAction {
    private final RegistryScreenRepository repo;
    private final RegistryApplicationRepository applications;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryScreenAction(RegistryScreenRepository repo, RegistryApplicationRepository applications, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.applications=applications; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenResponse execute(CreateRegistryScreenCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        applications.findByIdAndWorkspaceId(c.applicationId(), c.workspaceId()).orElseThrow(() -> TraceabilityExceptions.applicationNotFound(c.applicationId()));
        return RegistryScreenResponse.from(repo.save(RegistryScreen.create(c.applicationId(), c.projectId(), c.code().trim(), c.name().trim(), c.routePath())));
    }
}
