package com.company.scopery.modules.notification.advanced.alert.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertRuleResponse;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertRuleRepository;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AlertRuleQueryService {
    private final AlertRuleRepository rules; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public AlertRuleQueryService(AlertRuleRepository rules, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.rules=rules; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<AlertRuleResponse> list(UUID workspaceId) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.ALERT_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return rules.findByWorkspaceId(workspaceId).stream().map(AlertRuleResponse::from).toList();
    }
}
