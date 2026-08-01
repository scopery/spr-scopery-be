package com.company.scopery.modules.traceability.commspec.application.action;

import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecificationRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveCommunicationSpecAction {

    private final CommunicationSpecificationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public ArchiveCommunicationSpecAction(CommunicationSpecificationRepository repo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceCreate(workspaceId);
        var existing = repo.findByIdAndApplicationId(id, applicationId)
                .orElseThrow(() -> TraceabilityExceptions.commSpecNotFound(id));
        repo.save(existing.archive());
        activityLogger.logSuccess(TraceabilityEntityTypes.COMMUNICATION_SPECIFICATION, id,
                TraceabilityActivityActions.COMM_SPEC_ARCHIVED, "Communication spec archived: " + existing.code());
    }
}
