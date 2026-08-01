package com.company.scopery.modules.traceability.commspec.application.action;

import com.company.scopery.modules.traceability.commspec.application.response.CommunicationSpecResponse;
import com.company.scopery.modules.traceability.commspec.domain.enums.CommunicationSpecStatus;
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
public class MarkCommunicationSpecReadyAction {

    private final CommunicationSpecificationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public MarkCommunicationSpecReadyAction(CommunicationSpecificationRepository repo,
                                            TraceabilityAuthorizationService authorization,
                                            TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CommunicationSpecResponse execute(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceCreate(workspaceId);
        var existing = repo.findByIdAndApplicationId(id, applicationId)
                .orElseThrow(() -> TraceabilityExceptions.commSpecNotFound(id));
        if (existing.triggerKey() == null || existing.triggerKey().isBlank()) {
            throw TraceabilityExceptions.commSpecNotReady("triggerKey is required to mark READY");
        }
        boolean hasChannel = (existing.inAppContractJson() != null && !existing.inAppContractJson().isBlank())
                || (existing.emailContractJson() != null && !existing.emailContractJson().isBlank());
        if (!hasChannel) {
            throw TraceabilityExceptions.commSpecNotReady("At least one of inAppContractJson or emailContractJson is required");
        }
        var saved = repo.save(existing.withStatus(CommunicationSpecStatus.READY));
        activityLogger.logSuccess(TraceabilityEntityTypes.COMMUNICATION_SPECIFICATION, saved.id(),
                TraceabilityActivityActions.COMM_SPEC_MARKED_READY, "Communication spec marked READY: " + saved.code());
        return CommunicationSpecResponse.from(saved);
    }
}
