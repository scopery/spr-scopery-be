package com.company.scopery.modules.traceability.screenspecdoc.application.action;

import com.company.scopery.modules.traceability.screenspecdoc.application.command.CreateRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocResponse;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocument;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryScreenSpecDocAction {

    private final RegistryScreenSpecDocumentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryScreenSpecDocAction(RegistryScreenSpecDocumentRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenSpecDocResponse execute(CreateRegistryScreenSpecDocCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        if (repo.existsByProjectIdAndDocumentCode(c.projectId(), c.documentCode())) {
            throw TraceabilityExceptions.screenSpecDocCodeExists(c.documentCode());
        }

        RegistryScreenSpecDocument saved = repo.save(RegistryScreenSpecDocument.create(
                c.projectId(),
                c.workspaceId(),
                c.documentCode(),
                c.documentName(),
                c.projectName(),
                c.systemName(),
                c.phaseName(),
                c.language(),
                c.overview(),
                c.figmaUrl()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_SPEC_DOCUMENT, saved.id(),
                TraceabilityActivityActions.SCREEN_SPEC_DOC_CREATED,
                "Screen spec document created: " + saved.documentCode());

        return RegistryScreenSpecDocResponse.from(saved);
    }
}
