package com.company.scopery.modules.traceability.application.application.action;
import com.company.scopery.modules.traceability.application.application.command.CreateRegistryApplicationCommand;
import com.company.scopery.modules.traceability.application.application.response.RegistryApplicationResponse;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplication;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryApplicationAction {
    private final RegistryApplicationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryApplicationAction(RegistryApplicationRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryApplicationResponse execute(CreateRegistryApplicationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryApplicationResponse.from(repo.save(RegistryApplication.create(c.workspaceId(), c.code().trim(), c.name().trim(), c.description(), c.ownerUserId())));
    }
}
