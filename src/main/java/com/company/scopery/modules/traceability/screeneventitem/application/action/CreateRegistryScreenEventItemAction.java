package com.company.scopery.modules.traceability.screeneventitem.application.action;

import com.company.scopery.modules.traceability.screeneventitem.application.command.CreateRegistryScreenEventItemCommand;
import com.company.scopery.modules.traceability.screeneventitem.application.response.RegistryScreenEventItemResponse;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryScreenEventItemAction {

    private final RegistryScreenEventItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryScreenEventItemAction(RegistryScreenEventItemRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenEventItemResponse execute(CreateRegistryScreenEventItemCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        RegistryScreenEventItem saved = repo.save(RegistryScreenEventItem.create(
                c.screenId(), c.workspaceId(), c.modeId(), c.triggerFieldId(),
                c.triggerActionCode(), c.title(), c.content(), c.conditionNote(),
                c.targetScreenId(), c.targetModeCode(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_EVENT_ITEM, saved.id(),
                TraceabilityActivityActions.SCREEN_EVENT_ITEM_CREATED,
                "Screen event item created: " + saved.id());

        return RegistryScreenEventItemResponse.from(saved);
    }
}
