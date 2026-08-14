package com.company.scopery.modules.traceability.screeneventitem.application.action;

import com.company.scopery.modules.traceability.screeneventitem.application.command.UpdateRegistryScreenEventItemCommand;
import com.company.scopery.modules.traceability.screeneventitem.application.response.RegistryScreenEventItemResponse;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryScreenEventItemAction {

    private final RegistryScreenEventItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryScreenEventItemAction(RegistryScreenEventItemRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenEventItemResponse execute(UpdateRegistryScreenEventItemCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var item = repo.findByIdAndWorkspaceId(c.eventItemId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenEventItemNotFound(c.eventItemId()));

        var saved = repo.save(item.withUpdated(
                c.modeId(), c.triggerFieldId(), c.triggerActionCode(), c.title(), c.content(),
                c.conditionNote(), c.targetScreenId(), c.targetModeCode(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_EVENT_ITEM, saved.id(),
                TraceabilityActivityActions.SCREEN_EVENT_ITEM_UPDATED,
                "Screen event item updated: " + saved.id());

        return RegistryScreenEventItemResponse.from(saved);
    }
}
