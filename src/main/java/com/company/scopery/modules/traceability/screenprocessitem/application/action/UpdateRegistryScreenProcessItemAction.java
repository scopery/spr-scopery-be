package com.company.scopery.modules.traceability.screenprocessitem.application.action;

import com.company.scopery.modules.traceability.screenprocessitem.application.command.UpdateRegistryScreenProcessItemCommand;
import com.company.scopery.modules.traceability.screenprocessitem.application.response.RegistryScreenProcessItemResponse;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryScreenProcessItemAction {

    private final RegistryScreenProcessItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryScreenProcessItemAction(RegistryScreenProcessItemRepository repo,
                                                 TraceabilityAuthorizationService authorization,
                                                 TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenProcessItemResponse execute(UpdateRegistryScreenProcessItemCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var item = repo.findByIdAndWorkspaceId(c.processItemId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenProcessItemNotFound(c.processItemId()));

        var saved = repo.save(item.withUpdated(
                c.modeId(), c.targetFieldId(), c.title(), c.content(),
                c.sourceTable(), c.conditionNote(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_PROCESS_ITEM, saved.id(),
                TraceabilityActivityActions.SCREEN_PROCESS_ITEM_UPDATED,
                "Screen process item updated: " + saved.id());

        return RegistryScreenProcessItemResponse.from(saved);
    }
}
