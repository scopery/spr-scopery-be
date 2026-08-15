package com.company.scopery.modules.traceability.screencomponent.application.action;

import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class UnlinkScreenComponentAction {

    private final ScreenComponentRepository repo;
    private final RegistryComponentFieldRepository componentFieldRepo;
    private final RegistryScreenFieldRepository screenFieldRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkScreenComponentAction(ScreenComponentRepository repo,
                                       RegistryComponentFieldRepository componentFieldRepo,
                                       RegistryScreenFieldRepository screenFieldRepo,
                                       TraceabilityAuthorizationService authorization,
                                       TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.componentFieldRepo = componentFieldRepo;
        this.screenFieldRepo = screenFieldRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID screenId, UUID componentId) {
        authorization.requireWorkspaceCreate(workspaceId);

        if (!repo.existsByScreenIdAndComponentId(screenId, componentId)) {
            throw TraceabilityExceptions.screenComponentNotFound(screenId, componentId);
        }

        // Cascade delete screen_fields that originated from this component's fields
        List<UUID> cfIds = componentFieldRepo.findByComponentIdOrderByDisplayOrderAsc(componentId)
                .stream().map(RegistryComponentField::id).toList();
        if (!cfIds.isEmpty()) {
            List<UUID> toDelete = screenFieldRepo.findByScreenIdAndComponentFieldIdIn(screenId, cfIds)
                    .stream().map(RegistryScreenField::id).toList();
            screenFieldRepo.deleteAllByIds(toDelete);
        }

        repo.deleteByScreenIdAndComponentId(screenId, componentId);

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_COMPONENT, screenId,
                TraceabilityActivityActions.SCREEN_COMPONENT_UNLINKED, "Component unlinked from screen");
    }
}
