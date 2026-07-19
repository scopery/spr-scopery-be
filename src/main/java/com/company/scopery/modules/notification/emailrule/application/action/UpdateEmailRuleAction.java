package com.company.scopery.modules.notification.emailrule.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
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

import java.util.Map;
import java.util.UUID;

@Component
public class UpdateEmailRuleAction {

    private final EmailRuleRepository ruleRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public UpdateEmailRuleAction(EmailRuleRepository ruleRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                  IamSystemAuthorizationService systemAuthorizationService,
                                  NotificationActivityLogger activityLogger,
                                  ImmutableAuditEventService auditEventService) {
        this.ruleRepository = ruleRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public EmailRuleResponse execute(UpdateEmailRuleCommand cmd) {
        EmailRule rule = findOrThrow(cmd.id());
        UUID actorId = authorize(rule);
        if (rule.status() == EmailRuleStatus.DELETED) {
            throw NotificationExceptions.emailRuleNotFound(cmd.id());
        }
        boolean wasMandatory = rule.mandatory();
        boolean wasSensitive = rule.allowSensitiveVariables();
        boolean mandatory = cmd.mandatory() != null ? cmd.mandatory() : wasMandatory;
        boolean allowSensitive = cmd.allowSensitiveVariables() != null
                ? cmd.allowSensitiveVariables() : wasSensitive;
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());
        rule.update(cmd.name(), cmd.description(), strategy, cmd.recipientConfigJson(), cmd.priority(),
                mandatory, allowSensitive);
        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.UPDATE_EMAIL_RULE,
                "Email rule updated: " + rule.code());
        if (rule.mandatory() && !wasMandatory) {
            activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                    NotificationActivityActions.MARK_EMAIL_RULE_MANDATORY,
                    "Email rule marked mandatory: " + rule.code());
            auditEventService.record(AuditEventType.NOTIFICATION_RULE_MANDATORY_SET, actorId, "USER",
                    NotificationEntityTypes.EMAIL_RULE, rule.id(), null, rule.workspaceId(),
                    Map.of("mandatory", false), Map.of("mandatory", true), "Mandatory notice enabled");
        }
        if (rule.allowSensitiveVariables() && !wasSensitive) {
            activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                    NotificationActivityActions.ALLOW_EMAIL_RULE_SENSITIVE_VARIABLES,
                    "Email rule allows sensitive variables: " + rule.code());
            auditEventService.record(AuditEventType.NOTIFICATION_RULE_SENSITIVE_ALLOWED, actorId, "USER",
                    NotificationEntityTypes.EMAIL_RULE, rule.id(), null, rule.workspaceId(),
                    Map.of("allowSensitiveVariables", false), Map.of("allowSensitiveVariables", true),
                    "Sensitive variables allowed");
        }
        return EmailRuleResponse.from(rule);
    }

    private EmailRule findOrThrow(UUID id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailRuleNotFound(id));
    }

    private UUID authorize(EmailRule rule) {
        if (rule.scope() == EmailRuleScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_RULE.legacyRightCode());
            return currentUserAuthorizationService.resolveCurrentUser().id();
        }
        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                rule.workspaceId(), actorId, IamAuthorities.NOTIFICATION_MANAGE_RULE);
        return actorId;
    }
}
