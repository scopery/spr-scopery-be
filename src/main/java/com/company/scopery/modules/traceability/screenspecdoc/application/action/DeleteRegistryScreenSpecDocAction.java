package com.company.scopery.modules.traceability.screenspecdoc.application.action;

import com.company.scopery.modules.traceability.screenspecdoc.application.command.DeleteRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteRegistryScreenSpecDocAction {

    private final RegistryScreenSpecDocumentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryScreenSpecDocAction(RegistryScreenSpecDocumentRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteRegistryScreenSpecDocCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        repo.findByIdAndWorkspaceId(c.documentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(c.documentId()));

        repo.delete(c.documentId());

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_SPEC_DOCUMENT, c.documentId(),
                TraceabilityActivityActions.SCREEN_SPEC_DOC_DELETED,
                "Screen spec document deleted: " + c.documentId());
    }
}
