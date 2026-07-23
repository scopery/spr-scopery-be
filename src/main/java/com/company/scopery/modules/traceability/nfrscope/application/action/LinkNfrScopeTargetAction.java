package com.company.scopery.modules.traceability.nfrscope.application.action;

import com.company.scopery.modules.traceability.nfrscope.application.command.LinkNfrScopeTargetCommand;
import com.company.scopery.modules.traceability.nfrscope.application.response.NfrScopeTargetResponse;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTarget;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTargetRepository;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkNfrScopeTargetAction {

    private final NfrScopeTargetRepository repo;
    private final NonFunctionalItemRepository nfrRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkNfrScopeTargetAction(NfrScopeTargetRepository repo,
                                     NonFunctionalItemRepository nfrRepo,
                                     TraceabilityAuthorizationService authorization,
                                     TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.nfrRepo = nfrRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public NfrScopeTargetResponse execute(LinkNfrScopeTargetCommand c) {
        authorization.requireCreate(c.projectId());

        nfrRepo.findByIdAndProjectId(c.nfrId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.nonFunctionalItemNotFound(c.nfrId()));

        if (repo.existsByNfrIdAndTargetId(c.nfrId(), c.targetId())) {
            throw TraceabilityExceptions.nfrScopeTargetDuplicate();
        }

        NfrScopeTarget saved = repo.save(NfrScopeTarget.create(c.nfrId(), c.targetId(), c.targetType()));

        activityLogger.logSuccess(TraceabilityEntityTypes.NFR_SCOPE_TARGET, c.nfrId(),
                TraceabilityActivityActions.NFR_SCOPE_TARGET_LINKED,
                "Scope target linked to NFR: " + c.targetId());

        return NfrScopeTargetResponse.from(saved);
    }
}
