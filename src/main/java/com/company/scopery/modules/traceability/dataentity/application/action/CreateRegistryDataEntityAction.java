package com.company.scopery.modules.traceability.dataentity.application.action;
import com.company.scopery.modules.traceability.dataentity.application.command.CreateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryDataEntityAction {
    private final RegistryDataEntityRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryDataEntityAction(RegistryDataEntityRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional
    public RegistryDataEntityResponse execute(CreateRegistryDataEntityCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryDataEntityResponse.from(repo.save(RegistryDataEntity.create(c.applicationId(), c.workspaceId(), c.moduleId(), c.code().trim(), c.name().trim(), c.description(), c.tableName())));
    }
}
