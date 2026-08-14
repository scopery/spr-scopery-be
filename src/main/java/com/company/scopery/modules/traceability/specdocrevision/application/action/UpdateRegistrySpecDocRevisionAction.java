package com.company.scopery.modules.traceability.specdocrevision.application.action;

import com.company.scopery.modules.traceability.specdocrevision.application.command.UpdateRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.application.response.RegistrySpecDocRevisionResponse;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistrySpecDocRevisionAction {

    private final RegistrySpecDocRevisionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistrySpecDocRevisionAction(RegistrySpecDocRevisionRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistrySpecDocRevisionResponse execute(UpdateRegistrySpecDocRevisionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var revision = repo.findByIdAndWorkspaceId(c.revisionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.specDocRevisionNotFound(c.revisionId()));

        var saved = repo.save(revision.withUpdated(
                c.revisionNo(),
                c.targetSheetName(),
                c.details(),
                c.personInCharge(),
                c.color(),
                c.changedAt(),
                c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SPEC_DOC_REVISION, saved.id(),
                TraceabilityActivityActions.SPEC_DOC_REVISION_UPDATED,
                "Spec doc revision updated: " + saved.revisionNo());

        return RegistrySpecDocRevisionResponse.from(saved);
    }
}
