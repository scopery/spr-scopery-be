package com.company.scopery.modules.notification.advanced.alert.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.alert.application.command.AcknowledgeAlertEventCommand;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertEventResponse;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertEventRepository;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AcknowledgeAlertEventAction {
    private final AlertEventRepository events; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public AcknowledgeAlertEventAction(AlertEventRepository events, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.events=events; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public AlertEventResponse execute(AcknowledgeAlertEventCommand c) {
        try { iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.ALERT_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        var event = events.findByIdAndWorkspaceId(c.alertEventId(), c.workspaceId())
                .orElseThrow(() -> AdvancedNotificationExceptions.alertEventNotFound(c.alertEventId()));
        return AlertEventResponse.from(events.save(event.acknowledge()));
    }
}
