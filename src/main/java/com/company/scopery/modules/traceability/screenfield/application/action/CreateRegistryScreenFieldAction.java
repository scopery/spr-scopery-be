package com.company.scopery.modules.traceability.screenfield.application.action;
import com.company.scopery.modules.traceability.screenfield.application.command.CreateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.response.RegistryScreenFieldResponse;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryScreenFieldAction {
    private final RegistryScreenFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryScreenFieldAction(RegistryScreenFieldRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenFieldResponse execute(CreateRegistryScreenFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryScreenFieldResponse.from(repo.save(RegistryScreenField.create(c.screenId(), c.sectionId(), c.workspaceId(),
                c.fieldKey().trim(), c.label().trim(), c.fieldType().trim(), c.description(), c.required(), c.displayOrder())));
    }
}
