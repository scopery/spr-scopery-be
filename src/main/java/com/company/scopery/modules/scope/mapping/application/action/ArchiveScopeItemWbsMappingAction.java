package com.company.scopery.modules.scope.mapping.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.mapping.application.command.ArchiveScopeItemWbsMappingCommand;
import com.company.scopery.modules.scope.mapping.application.response.ScopeItemWbsMappingResponse;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMappingRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveScopeItemWbsMappingAction {
    private final ScopeItemWbsMappingRepository mappings;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScopeActivityLogger activityLogger;
    public ArchiveScopeItemWbsMappingAction(ScopeItemWbsMappingRepository mappings, ScopeAuthorizationService authorization,
                                            CurrentUserAuthorizationService currentUser, ScopeActivityLogger activityLogger) {
        this.mappings = mappings; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public ScopeItemWbsMappingResponse execute(ArchiveScopeItemWbsMappingCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var mapping = mappings.findByIdAndProjectId(command.mappingId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.wbsMappingNotFound(command.mappingId()));
        try {
            mapping = mappings.save(mapping.archive(actor.id()));
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        activityLogger.logSuccess(ScopeEntityTypes.WBS_MAPPING, mapping.id(), ScopeActivityActions.WBS_MAPPING_ARCHIVED,
                "WBS mapping archived");
        return ScopeItemWbsMappingResponse.from(mapping);
    }
}
