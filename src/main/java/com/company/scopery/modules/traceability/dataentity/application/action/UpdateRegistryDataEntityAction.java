package com.company.scopery.modules.traceability.dataentity.application.action;
import com.company.scopery.modules.traceability.dataentity.application.command.UpdateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryDataEntityAction {
    private final RegistryDataEntityRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryDataEntityAction(RegistryDataEntityRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryDataEntityResponse execute(UpdateRegistryDataEntityCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var entity = repo.findByIdAndWorkspaceId(c.dataEntityId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.dataEntityNotFound(c.dataEntityId()));
        return RegistryDataEntityResponse.from(repo.save(entity.withUpdated(c.moduleId(), c.name(), c.description(), c.tableName())));
    }
}
