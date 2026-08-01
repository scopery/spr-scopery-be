package com.company.scopery.modules.traceability.commspec.application.action;

import com.company.scopery.modules.traceability.commspec.application.command.UpdateCommunicationSpecCommand;
import com.company.scopery.modules.traceability.commspec.application.response.CommunicationSpecResponse;
import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecificationRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateCommunicationSpecAction {

    private final CommunicationSpecificationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateCommunicationSpecAction(CommunicationSpecificationRepository repo,
                                         TraceabilityAuthorizationService authorization,
                                         TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CommunicationSpecResponse execute(UpdateCommunicationSpecCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var existing = repo.findByIdAndApplicationId(c.communicationSpecId(), c.applicationId())
                .orElseThrow(() -> TraceabilityExceptions.commSpecNotFound(c.communicationSpecId()));
        var saved = repo.save(existing.withUpdated(
                c.name().trim(), c.description(),
                c.triggerName(), c.triggerKey(), c.triggerTiming(),
                c.conditionJson(), c.suppressionConditionJson(), c.deliveryPolicyJson(),
                c.inAppContractJson(), c.emailContractJson(), c.recipientsJson(), c.ownerId()));
        activityLogger.logSuccess(TraceabilityEntityTypes.COMMUNICATION_SPECIFICATION, saved.id(),
                TraceabilityActivityActions.COMM_SPEC_UPDATED, "Communication spec updated: " + saved.code());
        return CommunicationSpecResponse.from(saved);
    }
}
