package com.company.scopery.modules.notification.emailrule.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailrule.application.command.CreateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;
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
public class CreateEmailRuleAction {

    private final EmailRuleRepository ruleRepository;
    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public CreateEmailRuleAction(EmailRuleRepository ruleRepository,
                                  EmailTemplateRepository templateRepository,
                                  EventDefinitionRepository eventDefinitionRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                  IamSystemAuthorizationService systemAuthorizationService,
                                  NotificationActivityLogger activityLogger,
                                  ImmutableAuditEventService auditEventService) {
        this.ruleRepository = ruleRepository;
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public EmailRuleResponse execute(CreateEmailRuleCommand cmd) {
        EmailRuleScope scope = NotificationEnumParser.parseRuleScope(cmd.scope());
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());
        UUID actorId = authorize(scope, cmd.workspaceId());

        eventDefinitionRepository.findById(cmd.eventDefinitionId())
                .filter(e -> e.status() == EventDefinitionStatus.ACTIVE)
                .orElseThrow(() -> NotificationExceptions.emailRuleEventDefinitionNotFound(cmd.eventDefinitionId()));

        templateRepository.findById(cmd.templateId())
                .filter(t -> t.status() == EmailTemplateStatus.ACTIVE)
                .orElseThrow(() -> NotificationExceptions.emailRuleTemplateNotFound(cmd.templateId()));

        if (ruleRepository.existsByCode(cmd.code().trim().toUpperCase())) {
            throw NotificationExceptions.emailRuleCodeAlreadyExists(cmd.code());
        }

        EmailRule rule = scope == EmailRuleScope.SYSTEM
                ? EmailRule.createSystem(cmd.code(), cmd.name(), cmd.description(),
                        cmd.eventDefinitionId(), cmd.templateId(), strategy, cmd.recipientConfigJson(),
                        cmd.priority(), cmd.mandatory(), cmd.allowSensitiveVariables())
                : EmailRule.createWorkspace(cmd.code(), cmd.name(), cmd.description(),
                        cmd.workspaceId(), cmd.eventDefinitionId(), cmd.templateId(),
                        strategy, cmd.recipientConfigJson(), cmd.priority(),
                        cmd.mandatory(), cmd.allowSensitiveVariables());

        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.CREATE_EMAIL_RULE,
                "Email rule created: " + rule.code());
        logSensitiveFlags(rule, actorId, false, false);
        return EmailRuleResponse.from(rule);
    }

    private UUID authorize(EmailRuleScope scope, UUID workspaceId) {
        if (scope == EmailRuleScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_RULE.legacyRightCode());
            return currentUserAuthorizationService.resolveCurrentUser().id();
        }
        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                workspaceId, actorId, IamAuthorities.NOTIFICATION_MANAGE_RULE);
        return actorId;
    }

    private void logSensitiveFlags(EmailRule rule, UUID actorId, boolean wasMandatory, boolean wasSensitive) {
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
    }
}
