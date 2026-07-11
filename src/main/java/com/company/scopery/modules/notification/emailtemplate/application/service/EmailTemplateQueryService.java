package com.company.scopery.modules.notification.emailtemplate.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtemplate.application.query.SearchEmailTemplatesQuery;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateSearchCriteria;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailTemplateQueryService {

    private final EmailTemplateRepository templateRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;

    public EmailTemplateQueryService(EmailTemplateRepository templateRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService workspaceIamIntegrationService) {
        this.templateRepository = templateRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getTemplate(UUID id) {
        EmailTemplate template = findOrThrow(id);
        authorizeView(template);
        return EmailTemplateResponse.from(template);
    }

    @Transactional(readOnly = true)
    public List<EmailTemplateVersionResponse> getVersions(UUID templateId) {
        authorizeView(findOrThrow(templateId));
        return templateRepository.findVersionsByTemplateId(templateId)
                .stream().map(EmailTemplateVersionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EmailTemplateVersionResponse getVersion(UUID versionId) {
        var version = templateRepository.findVersionById(versionId)
                .orElseThrow(() -> NotificationExceptions.emailTemplateVersionNotFound(versionId));
        authorizeView(findOrThrow(version.templateId()));
        return EmailTemplateVersionResponse.from(version);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailTemplateResponse> searchTemplates(SearchEmailTemplatesQuery query) {
        EmailTemplateScope scope = query.scope() != null
                ? NotificationEnumParser.parseTemplateScope(query.scope()) : null;
        EmailTemplateStatus status = query.status() != null
                ? NotificationEnumParser.parseTemplateStatus(query.status()) : null;

        UUID workspaceId = query.workspaceId();
        if (workspaceId != null) {
            requireWorkspaceView(workspaceId);
        } else {
            // Without a workspace context, only the shared system catalog is browsable —
            // otherwise this would enumerate every workspace's email templates at once.
            scope = EmailTemplateScope.SYSTEM;
        }

        EmailTemplateSearchCriteria criteria = new EmailTemplateSearchCriteria(
                query.keyword(), scope, status,
                workspaceId, query.eventDefinitionId(),
                query.page(), query.size());

        List<EmailTemplateResponse> items = templateRepository.findAll(criteria)
                .stream().map(EmailTemplateResponse::from).toList();
        long total = templateRepository.countAll(criteria);
        int totalPages = query.size() == 0 ? 1 : (int) Math.ceil((double) total / query.size());
        return new PageResponse<>(items, query.page(), query.size(), total, totalPages,
                query.page() == 0, query.page() >= totalPages - 1);
    }

    private EmailTemplate findOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailTemplateNotFound(id));
    }

    private void authorizeView(EmailTemplate template) {
        if (template.scope() == EmailTemplateScope.WORKSPACE) {
            requireWorkspaceView(template.workspaceId());
        }
    }

    private void requireWorkspaceView(UUID workspaceId) {
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                workspaceId, actorId, IamAuthorities.NOTIFICATION_VIEW_TEMPLATE);
    }
}
