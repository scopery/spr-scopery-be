package com.company.scopery.modules.traceability.specdocrevision.application.action;

import com.company.scopery.modules.traceability.specdocrevision.application.command.DeleteRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteRegistrySpecDocRevisionAction {

    private final RegistrySpecDocRevisionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistrySpecDocRevisionAction(RegistrySpecDocRevisionRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteRegistrySpecDocRevisionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        repo.findByIdAndWorkspaceId(c.revisionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.specDocRevisionNotFound(c.revisionId()));

        repo.delete(c.revisionId());

        activityLogger.logSuccess(TraceabilityEntityTypes.SPEC_DOC_REVISION, c.revisionId(),
                TraceabilityActivityActions.SPEC_DOC_REVISION_DELETED,
                "Spec doc revision deleted: " + c.revisionId());
    }
}
