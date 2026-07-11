package com.company.scopery.modules.notification.emailtemplate.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtemplate.application.command.UpdateEmailTemplateCommand;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateEmailTemplateAction {

    private final EmailTemplateRepository templateRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final NotificationActivityLogger activityLogger;

    public UpdateEmailTemplateAction(EmailTemplateRepository templateRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                      IamSystemAuthorizationService systemAuthorizationService,
                                      NotificationActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailTemplateResponse execute(UpdateEmailTemplateCommand cmd) {
        EmailTemplate template = findOrThrow(cmd.id());
        authorize(template);
        if (template.status() == EmailTemplateStatus.DELETED) {
            throw NotificationExceptions.emailTemplateDeleted(cmd.id());
        }
        template.update(cmd.name(), cmd.description());
        template = templateRepository.save(template);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE, template.id(),
                NotificationActivityActions.UPDATE_EMAIL_TEMPLATE,
                "Email template updated: " + template.code().value());
        return EmailTemplateResponse.from(template);
    }

    private EmailTemplate findOrThrow(java.util.UUID id) {
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
