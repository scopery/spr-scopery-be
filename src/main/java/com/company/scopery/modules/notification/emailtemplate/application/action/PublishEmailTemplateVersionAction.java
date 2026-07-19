package com.company.scopery.modules.notification.emailtemplate.application.action;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateVariableValidator;
import com.company.scopery.modules.notification.emailtemplate.application.command.PublishEmailTemplateVersionCommand;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PublishEmailTemplateVersionAction {

    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final EmailTemplateVariableValidator variableValidator;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public PublishEmailTemplateVersionAction(EmailTemplateRepository templateRepository,
                                              EventDefinitionRepository eventDefinitionRepository,
                                              EmailTemplateVariableValidator variableValidator,
                                              CurrentUserAuthorizationService currentUserAuthorizationService,
                                              WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                              IamSystemAuthorizationService systemAuthorizationService,
                                              NotificationActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.variableValidator = variableValidator;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailTemplateVersionResponse execute(PublishEmailTemplateVersionCommand cmd) {
        EmailTemplate template = findOrThrow(cmd.templateId());
        authorize(template);
        EmailTemplateVersion version = templateRepository.findVersionById(cmd.versionId())
                .orElseThrow(() -> NotificationExceptions.emailTemplateVersionNotFound(cmd.versionId()));

        List<EventVariable> variables = eventDefinitionRepository.findVariablesByEventDefinitionId(template.eventDefinitionId());
        Set<String> allowedPaths = variables.stream().map(EventVariable::variablePath).collect(Collectors.toSet());
        Set<String> sensitivePaths = variables.stream()
                .filter(EventVariable::sensitive)
                .map(EventVariable::variablePath)
                .collect(Collectors.toSet());

        boolean allowSensitiveInBody = cmd.allowSensitiveVariables();
        variableValidator.validate(version.subjectTemplate(), version.htmlBodyTemplate(),
                version.textBodyTemplate(), allowedPaths, sensitivePaths, allowSensitiveInBody);

        UUID publisherId = null;
        try {
            publisherId = currentUserAuthorizationService.resolveCurrentUser().id();
        } catch (Exception ignored) {
            // Seeders / system paths may publish without a request principal.
        }
        version.publish(publisherId);
        version = templateRepository.saveVersion(version);

        template.publishVersion(version.id());
        templateRepository.save(template);

        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE_VERSION, version.id(),
                NotificationActivityActions.PUBLISH_EMAIL_TEMPLATE_VERSION,
                "Version " + version.versionNumber() + " published for template " + template.code().value());
        return EmailTemplateVersionResponse.from(version);
    }

    private EmailTemplate findOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailTemplateNotFound(id));
    }

    private void authorize(EmailTemplate template) {
        if (template.scope() == EmailTemplateScope.SYSTEM) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_NOTIFICATION_MANAGE_TEMPLATE.legacyRightCode());
        } else {
            var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    template.workspaceId(), actorId, IamAuthorities.NOTIFICATION_MANAGE_TEMPLATE);
        }
    }
}
