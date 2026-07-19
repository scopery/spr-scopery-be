package com.company.scopery.modules.scope.deliverable.application.action;
import com.company.scopery.modules.scope.deliverable.application.command.ChangeDeliverableStatusCommand;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableStatus;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ChangeDeliverableStatusAction {
    private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public ChangeDeliverableStatusAction(DeliverableRepository deliverables, ScopeAuthorizationService authorization,
                                         ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableResponse execute(ChangeDeliverableStatusCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        var d = deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        DeliverableStatus target = ScopeEnumParser.parseRequired(DeliverableStatus.class, command.status(), "status");
        try {
            d = deliverables.save(d.changeStatus(target));
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        activityLogger.logSuccess(ScopeEntityTypes.DELIVERABLE, d.id(), ScopeActivityActions.DELIVERABLE_STATUS_CHANGED,
                "Deliverable status changed to " + target.name());
        return DeliverableResponse.from(d);
    }
}
