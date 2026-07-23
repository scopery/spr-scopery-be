package com.company.scopery.modules.traceability.nfrscope.application.action;

import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTargetRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnlinkNfrScopeTargetAction {

    private final NfrScopeTargetRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkNfrScopeTargetAction(NfrScopeTargetRepository repo,
                                       TraceabilityAuthorizationService authorization,
                                       TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID nfrId, UUID targetId) {
        authorization.requireCreate(projectId);

        if (!repo.existsByNfrIdAndTargetId(nfrId, targetId)) {
            throw TraceabilityExceptions.nfrScopeTargetNotFound(nfrId, targetId);
        }

        repo.deleteByNfrIdAndTargetId(nfrId, targetId);

        activityLogger.logSuccess(TraceabilityEntityTypes.NFR_SCOPE_TARGET, nfrId,
                TraceabilityActivityActions.NFR_SCOPE_TARGET_UNLINKED,
                "Scope target unlinked from NFR: " + targetId);
    }
}
