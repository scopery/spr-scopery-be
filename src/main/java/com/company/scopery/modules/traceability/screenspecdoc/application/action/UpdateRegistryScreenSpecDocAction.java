package com.company.scopery.modules.traceability.screenspecdoc.application.action;

import com.company.scopery.modules.traceability.screenspecdoc.application.command.UpdateRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocResponse;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryScreenSpecDocAction {

    private final RegistryScreenSpecDocumentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryScreenSpecDocAction(RegistryScreenSpecDocumentRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenSpecDocResponse execute(UpdateRegistryScreenSpecDocCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var doc = repo.findByIdAndWorkspaceId(c.documentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenSpecDocNotFound(c.documentId()));

        var saved = repo.save(doc.withUpdated(
                c.documentName(),
                c.projectName(),
                c.systemName(),
                c.phaseName(),
                c.language(),
                c.overview(),
                c.figmaUrl()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_SPEC_DOCUMENT, saved.id(),
                TraceabilityActivityActions.SCREEN_SPEC_DOC_UPDATED,
                "Screen spec document updated: " + saved.documentCode());

        return RegistryScreenSpecDocResponse.from(saved);
    }
}
