package com.company.scopery.modules.traceability.functionscreen.application.action;

import com.company.scopery.modules.traceability.functionscreen.application.command.LinkFunctionScreenCommand;
import com.company.scopery.modules.traceability.functionscreen.application.response.FunctionScreenResponse;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkFunctionScreenAction {

    private final FunctionScreenRepository repo;
    private final FunctionalItemRepository functionalItems;
    private final RegistryScreenRepository screens;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkFunctionScreenAction(FunctionScreenRepository repo,
                                    FunctionalItemRepository functionalItems,
                                    RegistryScreenRepository screens,
                                    TraceabilityAuthorizationService authorization,
                                    TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.functionalItems = functionalItems;
        this.screens = screens;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FunctionScreenResponse execute(LinkFunctionScreenCommand c) {
        authorization.requireCreate(c.projectId());

        functionalItems.findByIdAndProjectId(c.functionalItemId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionalItemId()));

        if (!screens.existsById(c.screenId())) {
            throw TraceabilityExceptions.screenNotFound(c.screenId());
        }

        if (repo.existsByFunctionIdAndScreenId(c.functionalItemId(), c.screenId())) {
            throw TraceabilityExceptions.functionScreenDuplicate();
        }

        FunctionScreen saved = repo.save(FunctionScreen.create(c.functionalItemId(), c.screenId(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_SCREEN, c.functionalItemId(),
                TraceabilityActivityActions.FUNCTION_SCREEN_LINKED, "Screen linked to function");

        return FunctionScreenResponse.from(saved);
    }
}
