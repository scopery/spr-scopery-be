package com.company.scopery.modules.traceability.screenaction.application.action;
import com.company.scopery.modules.traceability.screenaction.application.command.CreateRegistryScreenActionCommand;
import com.company.scopery.modules.traceability.screenaction.application.response.RegistryScreenActionResponse;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenAction;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenActionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryScreenActionAction {
    private final RegistryScreenActionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryScreenActionAction(RegistryScreenActionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenActionResponse execute(CreateRegistryScreenActionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryScreenActionResponse.from(repo.save(RegistryScreenAction.create(c.screenId(), c.workspaceId(),
                c.actionCode().trim(), c.name().trim(), c.actionType(), c.description(), c.displayOrder())));
    }
}
