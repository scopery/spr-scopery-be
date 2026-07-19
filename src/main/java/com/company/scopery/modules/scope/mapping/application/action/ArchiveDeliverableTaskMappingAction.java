package com.company.scopery.modules.scope.mapping.application.action;
import com.company.scopery.modules.scope.mapping.application.command.ArchiveDeliverableTaskMappingCommand;
import com.company.scopery.modules.scope.mapping.application.response.DeliverableTaskMappingResponse;
import com.company.scopery.modules.scope.mapping.domain.model.DeliverableTaskMappingRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveDeliverableTaskMappingAction {
    private final DeliverableTaskMappingRepository mappings;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public ArchiveDeliverableTaskMappingAction(DeliverableTaskMappingRepository mappings,
                                               ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.mappings = mappings; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableTaskMappingResponse execute(ArchiveDeliverableTaskMappingCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        var mapping = mappings.findByIdAndProjectId(command.mappingId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.taskMappingNotFound(command.mappingId()));
        try {
            mapping = mappings.save(mapping.archive());
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        activityLogger.logSuccess(ScopeEntityTypes.TASK_MAPPING, mapping.id(), ScopeActivityActions.TASK_MAPPING_ARCHIVED,
                "Task mapping archived");
        return DeliverableTaskMappingResponse.from(mapping);
    }
}
