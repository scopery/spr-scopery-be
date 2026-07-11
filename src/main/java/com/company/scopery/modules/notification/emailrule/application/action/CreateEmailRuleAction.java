package com.company.scopery.modules.notification.emailrule.application.action;

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

@Component
public class CreateEmailRuleAction {

    private final EmailRuleRepository ruleRepository;
    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public CreateEmailRuleAction(EmailRuleRepository ruleRepository,
                                  EmailTemplateRepository templateRepository,
                                  EventDefinitionRepository eventDefinitionRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                  IamSystemAuthorizationService systemAuthorizationService,
                                  NotificationActivityLogger activityLogger) {
        this.ruleRepository = ruleRepository;
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailRuleResponse execute(CreateEmailRuleCommand cmd) {
        EmailRuleScope scope = NotificationEnumParser.parseRuleScope(cmd.scope());
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());

        if (scope == EmailRuleScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_RULE.legacyRightCode());
        } else {
            var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    cmd.workspaceId(), actorId, IamAuthorities.NOTIFICATION_MANAGE_RULE);
        }

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
                        cmd.eventDefinitionId(), cmd.templateId(), strategy, cmd.recipientConfigJson(), cmd.priority())
                : EmailRule.createWorkspace(cmd.code(), cmd.name(), cmd.description(),
                        cmd.workspaceId(), cmd.eventDefinitionId(), cmd.templateId(),
                        strategy, cmd.recipientConfigJson(), cmd.priority());

        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.CREATE_EMAIL_RULE,
                "Email rule created: " + rule.code());
        return EmailRuleResponse.from(rule);
    }
}
