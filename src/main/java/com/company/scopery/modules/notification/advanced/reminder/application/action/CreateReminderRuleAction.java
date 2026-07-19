package com.company.scopery.modules.notification.advanced.reminder.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.reminder.application.command.CreateReminderRuleCommand;
import com.company.scopery.modules.notification.advanced.reminder.application.response.ReminderRuleResponse;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.*;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateReminderRuleAction {
    private final ReminderRuleRepository rules; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public CreateReminderRuleAction(ReminderRuleRepository rules, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.rules=rules; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public ReminderRuleResponse execute(CreateReminderRuleCommand c) {
        try { iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.REMINDER_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return ReminderRuleResponse.from(rules.save(ReminderRule.create(c.workspaceId(), c.ruleCode(), c.name(), c.conditionJson(), c.recipientRuleJson())));
    }
}
