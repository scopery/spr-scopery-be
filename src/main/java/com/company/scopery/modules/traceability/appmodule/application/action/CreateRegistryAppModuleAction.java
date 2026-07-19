package com.company.scopery.modules.traceability.appmodule.application.action;
import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryAppModuleAction {
    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryAppModuleAction(RegistryAppModuleRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryAppModuleResponse execute(CreateRegistryAppModuleCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryAppModuleResponse.from(repo.save(RegistryAppModule.create(c.applicationId(), c.workspaceId(), c.code().trim(), c.name().trim(), c.description())));
    }
}
