package com.company.scopery.modules.traceability.screencomponent.application.action;

import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.screencomponent.application.command.BindComponentToSectionCommand;
import com.company.scopery.modules.traceability.screencomponent.application.response.BindComponentToSectionResponse;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSectionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class BindComponentToSectionAction {

    private final ScreenComponentRepository screenComponentRepo;
    private final RegistryComponentFieldRepository componentFieldRepo;
    private final RegistryScreenFieldRepository screenFieldRepo;
    private final RegistryScreenSectionRepository sectionRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public BindComponentToSectionAction(ScreenComponentRepository screenComponentRepo,
                                         RegistryComponentFieldRepository componentFieldRepo,
                                         RegistryScreenFieldRepository screenFieldRepo,
                                         RegistryScreenSectionRepository sectionRepo,
                                         TraceabilityAuthorizationService authorization,
                                         TraceabilityActivityLogger activityLogger) {
        this.screenComponentRepo = screenComponentRepo;
        this.componentFieldRepo = componentFieldRepo;
        this.screenFieldRepo = screenFieldRepo;
        this.sectionRepo = sectionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BindComponentToSectionResponse execute(BindComponentToSectionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        // Validate section belongs to the screen
        var section = sectionRepo.findByIdAndWorkspaceId(c.sectionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSectionNotFound(c.sectionId()));
        if (!section.screenId().equals(c.screenId())) {
            throw TraceabilityExceptions.screenSectionNotFound(c.sectionId());
        }

        // Prevent duplicate link
        if (screenComponentRepo.existsByScreenIdAndComponentId(c.screenId(), c.componentId())) {
            throw TraceabilityExceptions.screenComponentDuplicate();
        }

        // Create link record
        screenComponentRepo.save(
                ScreenComponent.create(c.screenId(), c.componentId(), c.sectionId(), c.displayOrder(), c.note()));

        // Load and copy component fields as screen fields
        List<RegistryComponentField> componentFields =
                componentFieldRepo.findByComponentIdOrderByDisplayOrderAsc(c.componentId());

        List<String> importedKeys = componentFields.stream()
                .map(cf -> {
                    var screenField = RegistryScreenField.create(
                            c.screenId(), c.sectionId(), c.workspaceId(),
                            cf.fieldKey(), cf.label(), cf.fieldType(),
                            null, cf.required(), cf.displayOrder(),
                            null, null, cf.maxLength(), cf.remark(),
                            cf.id());
                    screenFieldRepo.save(screenField);
                    return cf.fieldKey();
                })
                .toList();

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_COMPONENT, c.screenId(),
                TraceabilityActivityActions.COMPONENT_BOUND_TO_SECTION,
                "Component " + c.componentId() + " bound to section " + c.sectionId()
                        + ", imported " + importedKeys.size() + " fields");

        return new BindComponentToSectionResponse(c.screenId(), c.sectionId(), c.componentId(),
                importedKeys.size(), importedKeys);
    }
}
