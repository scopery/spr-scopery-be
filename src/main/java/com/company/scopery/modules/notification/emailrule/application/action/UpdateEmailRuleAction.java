package com.company.scopery.modules.notification.emailrule.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailrule.application.command.UpdateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleStatus;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateEmailRuleAction {

    private final EmailRuleRepository ruleRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public UpdateEmailRuleAction(EmailRuleRepository ruleRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                  IamSystemAuthorizationService systemAuthorizationService,
                                  NotificationActivityLogger activityLogger) {
        this.ruleRepository = ruleRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailRuleResponse execute(UpdateEmailRuleCommand cmd) {
        EmailRule rule = findOrThrow(cmd.id());
        authorize(rule);
        if (rule.status() == EmailRuleStatus.DELETED) {
            throw NotificationExceptions.emailRuleNotFound(cmd.id());
        }
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());
        rule.update(cmd.name(), cmd.description(), strategy, cmd.recipientConfigJson(), cmd.priority());
        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.UPDATE_EMAIL_RULE,
                "Email rule updated: " + rule.code());
        return EmailRuleResponse.from(rule);
    }

    private EmailRule findOrThrow(java.util.UUID id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailRuleNotFound(id));
    }

    private void authorize(EmailRule rule) {
        if (rule.scope() == EmailRuleScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_RULE.legacyRightCode());
        } else {
            var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    rule.workspaceId(), actorId, IamAuthorities.NOTIFICATION_MANAGE_RULE);
        }
    }
}
