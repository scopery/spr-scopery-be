package com.company.scopery.modules.traceability.screenprocessitem.application.action;

import com.company.scopery.modules.traceability.screenprocessitem.application.command.CreateRegistryScreenProcessItemCommand;
import com.company.scopery.modules.traceability.screenprocessitem.application.response.RegistryScreenProcessItemResponse;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryScreenProcessItemAction {

    private final RegistryScreenProcessItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryScreenProcessItemAction(RegistryScreenProcessItemRepository repo,
                                                 TraceabilityAuthorizationService authorization,
                                                 TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenProcessItemResponse execute(CreateRegistryScreenProcessItemCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        RegistryScreenProcessItem saved = repo.save(RegistryScreenProcessItem.create(
                c.screenId(), c.workspaceId(), c.modeId(), c.targetFieldId(),
                c.title(), c.content(), c.sourceTable(), c.conditionNote(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_PROCESS_ITEM, saved.id(),
                TraceabilityActivityActions.SCREEN_PROCESS_ITEM_CREATED,
                "Screen process item created: " + saved.id());

        return RegistryScreenProcessItemResponse.from(saved);
    }
}
