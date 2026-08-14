package com.company.scopery.modules.traceability.screenspecdoc.application.action;

import com.company.scopery.modules.traceability.screenspecdoc.application.command.AddScreenToSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.SpecDocScreenResponse;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddScreenToSpecDocAction {

    private final RegistryScreenSpecDocumentRepository docRepo;
    private final SpecDocScreenRepository screenRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public AddScreenToSpecDocAction(RegistryScreenSpecDocumentRepository docRepo,
                                    SpecDocScreenRepository screenRepo,
                                    TraceabilityAuthorizationService authorization,
                                    TraceabilityActivityLogger activityLogger) {
        this.docRepo = docRepo;
        this.screenRepo = screenRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SpecDocScreenResponse execute(AddScreenToSpecDocCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        docRepo.findByIdAndWorkspaceId(c.documentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(c.documentId()));

        if (screenRepo.existsByDocumentIdAndScreenId(c.documentId(), c.screenId())) {
            throw TraceabilityExceptions.specDocScreenDuplicate();
        }

        SpecDocScreen saved = screenRepo.save(
                new SpecDocScreen(c.documentId(), c.screenId(), c.displayOrder(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_SPEC_DOCUMENT, c.documentId(),
                TraceabilityActivityActions.SPEC_DOC_SCREEN_ADDED,
                "Screen added to spec doc: screenId=" + c.screenId());

        return SpecDocScreenResponse.from(saved);
    }
}
