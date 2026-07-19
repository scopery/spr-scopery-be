package com.company.scopery.modules.notification.advanced.alert.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertEventResponse;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertEventRepository;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AlertEventQueryService {
    private final AlertEventRepository events; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public AlertEventQueryService(AlertEventRepository events, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.events=events; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<AlertEventResponse> list(UUID workspaceId) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.ALERT_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return events.findByWorkspaceId(workspaceId).stream().map(AlertEventResponse::from).toList();
    }
}
