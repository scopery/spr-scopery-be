package com.company.scopery.modules.notification.emailtemplate.application.action;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtemplate.application.command.CreateEmailTemplateCommand;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.valueobject.EmailTemplateCode;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateEmailTemplateAction {

    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public CreateEmailTemplateAction(EmailTemplateRepository templateRepository,
                                      EventDefinitionRepository eventDefinitionRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                      IamSystemAuthorizationService systemAuthorizationService,
                                      NotificationActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailTemplateResponse execute(CreateEmailTemplateCommand cmd) {
        EmailTemplateScope scope = NotificationEnumParser.parseTemplateScope(cmd.scope());

        if (scope == EmailTemplateScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_TEMPLATE.legacyRightCode());
        } else {
            var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    cmd.workspaceId(), actorId, IamAuthorities.NOTIFICATION_MANAGE_TEMPLATE);
        }

        var eventDef = eventDefinitionRepository.findById(cmd.eventDefinitionId())
                .orElseThrow(() -> NotificationExceptions.emailTemplateEventDefinitionNotFound(cmd.eventDefinitionId()));
        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw NotificationExceptions.emailTemplateEventDefinitionNotActive(cmd.eventDefinitionId());
        }

        EmailTemplateCode code = EmailTemplateCode.of(cmd.code());
        if (templateRepository.existsByCode(code.value())) {
            throw NotificationExceptions.emailTemplateCodeAlreadyExists(code.value());
        }

        EmailTemplate template = scope == EmailTemplateScope.SYSTEM
                ? EmailTemplate.createSystem(code, cmd.name(), cmd.description(), cmd.eventDefinitionId())
                : EmailTemplate.createWorkspace(code, cmd.name(), cmd.description(),
                        cmd.workspaceId(), cmd.eventDefinitionId());

        template = templateRepository.save(template);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE, template.id(),
                NotificationActivityActions.CREATE_EMAIL_TEMPLATE,
                "Email template created: " + template.code().value());
        return EmailTemplateResponse.from(template);
    }
}
