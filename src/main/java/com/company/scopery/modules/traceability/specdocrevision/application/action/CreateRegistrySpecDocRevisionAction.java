package com.company.scopery.modules.traceability.specdocrevision.application.action;

import com.company.scopery.modules.traceability.specdocrevision.application.command.CreateRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.application.response.RegistrySpecDocRevisionResponse;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevision;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistrySpecDocRevisionAction {

    private final RegistrySpecDocRevisionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistrySpecDocRevisionAction(RegistrySpecDocRevisionRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistrySpecDocRevisionResponse execute(CreateRegistrySpecDocRevisionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        RegistrySpecDocRevision saved = repo.save(RegistrySpecDocRevision.create(
                c.documentId(),
                c.workspaceId(),
                c.revisionNo(),
                c.targetSheetName(),
                c.details(),
                c.personInCharge(),
                c.color(),
                c.changedAt(),
                c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SPEC_DOC_REVISION, saved.id(),
                TraceabilityActivityActions.SPEC_DOC_REVISION_CREATED,
                "Spec doc revision created: " + saved.revisionNo());

        return RegistrySpecDocRevisionResponse.from(saved);
    }
}
