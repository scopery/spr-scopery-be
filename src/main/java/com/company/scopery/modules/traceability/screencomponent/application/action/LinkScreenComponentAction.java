package com.company.scopery.modules.traceability.screencomponent.application.action;

import com.company.scopery.modules.traceability.screencomponent.application.command.LinkScreenComponentCommand;
import com.company.scopery.modules.traceability.screencomponent.application.response.ScreenComponentResponse;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkScreenComponentAction {

    private final ScreenComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkScreenComponentAction(ScreenComponentRepository repo,
                                     TraceabilityAuthorizationService authorization,
                                     TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScreenComponentResponse execute(LinkScreenComponentCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        if (repo.existsByScreenIdAndComponentId(c.screenId(), c.componentId())) {
            throw TraceabilityExceptions.screenComponentDuplicate();
        }

        ScreenComponent saved = repo.save(
                ScreenComponent.create(c.screenId(), c.componentId(), c.sectionId(), c.displayOrder(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_COMPONENT, c.screenId(),
                TraceabilityActivityActions.SCREEN_COMPONENT_LINKED, "Component linked to screen");

        return ScreenComponentResponse.from(saved);
    }
}
