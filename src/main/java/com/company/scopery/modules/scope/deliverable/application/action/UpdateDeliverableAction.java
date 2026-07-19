package com.company.scopery.modules.scope.deliverable.application.action;
import com.company.scopery.modules.scope.deliverable.application.command.UpdateDeliverableCommand;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
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
public class UpdateDeliverableAction {
    private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public UpdateDeliverableAction(DeliverableRepository deliverables, ScopeAuthorizationService authorization,
                                   ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableResponse execute(UpdateDeliverableCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        var d = deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        DeliverableType type = command.type() == null || command.type().isBlank()
                ? d.type() : ScopeEnumParser.parseRequired(DeliverableType.class, command.type(), "type");
        try {
            d = deliverables.save(d.update(type, command.title(), command.description()));
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        activityLogger.logSuccess(ScopeEntityTypes.DELIVERABLE, d.id(), ScopeActivityActions.DELIVERABLE_UPDATED, "Deliverable updated");
        return DeliverableResponse.from(d);
    }
}
