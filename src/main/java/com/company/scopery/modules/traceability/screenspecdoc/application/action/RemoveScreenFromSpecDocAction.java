package com.company.scopery.modules.traceability.screenspecdoc.application.action;

import com.company.scopery.modules.traceability.screenspecdoc.application.command.RemoveScreenFromSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveScreenFromSpecDocAction {

    private final RegistryScreenSpecDocumentRepository docRepo;
    private final SpecDocScreenRepository screenRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public RemoveScreenFromSpecDocAction(RegistryScreenSpecDocumentRepository docRepo,
                                         SpecDocScreenRepository screenRepo,
                                         TraceabilityAuthorizationService authorization,
                                         TraceabilityActivityLogger activityLogger) {
        this.docRepo = docRepo;
        this.screenRepo = screenRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveScreenFromSpecDocCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        docRepo.findByIdAndWorkspaceId(c.documentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(c.documentId()));

        if (!screenRepo.existsByDocumentIdAndScreenId(c.documentId(), c.screenId())) {
            throw TraceabilityExceptions.specDocScreenNotFound(c.documentId(), c.screenId());
        }

        screenRepo.deleteByDocumentIdAndScreenId(c.documentId(), c.screenId());

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_SPEC_DOCUMENT, c.documentId(),
                TraceabilityActivityActions.SPEC_DOC_SCREEN_REMOVED,
                "Screen removed from spec doc: screenId=" + c.screenId());
    }
}
