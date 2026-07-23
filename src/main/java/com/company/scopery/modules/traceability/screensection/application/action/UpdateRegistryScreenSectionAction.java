package com.company.scopery.modules.traceability.screensection.application.action;
import com.company.scopery.modules.traceability.screensection.application.command.UpdateRegistryScreenSectionCommand;
import com.company.scopery.modules.traceability.screensection.application.response.RegistryScreenSectionResponse;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryScreenSectionAction {
    private final RegistryScreenSectionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryScreenSectionAction(RegistryScreenSectionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryScreenSectionResponse execute(UpdateRegistryScreenSectionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var section = repo.findByIdAndWorkspaceId(c.sectionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSectionNotFound(c.sectionId()));
        return RegistryScreenSectionResponse.from(repo.save(section.withUpdated(c.name(), c.description(), c.displayOrder())));
    }
}
