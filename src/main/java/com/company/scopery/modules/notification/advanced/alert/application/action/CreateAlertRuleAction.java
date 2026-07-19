package com.company.scopery.modules.notification.advanced.alert.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.alert.application.command.CreateAlertRuleCommand;
import com.company.scopery.modules.notification.advanced.alert.application.response.AlertRuleResponse;
import com.company.scopery.modules.notification.advanced.alert.domain.model.*;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateAlertRuleAction {
    private final AlertRuleRepository rules; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public CreateAlertRuleAction(AlertRuleRepository rules, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.rules=rules; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public AlertRuleResponse execute(CreateAlertRuleCommand c) {
        try { iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.ALERT_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return AlertRuleResponse.from(rules.save(AlertRule.create(c.workspaceId(), c.ruleCode(), c.name(), c.category(), c.conditionJson(), Boolean.TRUE.equals(c.bypassQuietHours()))));
    }
}
