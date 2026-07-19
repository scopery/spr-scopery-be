package com.company.scopery.modules.traceability.screensection.application.action;
import com.company.scopery.modules.traceability.screensection.application.command.CreateRegistryScreenSectionCommand;
import com.company.scopery.modules.traceability.screensection.application.response.RegistryScreenSectionResponse;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSection;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryScreenSectionAction {
    private final RegistryScreenSectionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryScreenSectionAction(RegistryScreenSectionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenSectionResponse execute(CreateRegistryScreenSectionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryScreenSectionResponse.from(repo.save(RegistryScreenSection.create(c.screenId(), c.workspaceId(), c.name().trim(), c.description(), c.displayOrder())));
    }
}
