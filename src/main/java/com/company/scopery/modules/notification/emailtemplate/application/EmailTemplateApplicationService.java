package com.company.scopery.modules.notification.emailtemplate.application;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventVariable;
import com.company.scopery.modules.notification.emailtemplate.application.command.*;
import com.company.scopery.modules.notification.emailtemplate.application.query.SearchEmailTemplatesQuery;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateResponse;
import com.company.scopery.modules.notification.emailtemplate.application.response.EmailTemplateVersionResponse;
import com.company.scopery.modules.notification.emailtemplate.domain.*;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailTemplateApplicationService {

    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final EmailTemplateVariableValidator variableValidator;
    private final NotificationActivityLogger activityLogger;

    public EmailTemplateApplicationService(EmailTemplateRepository templateRepository,
                                            EventDefinitionRepository eventDefinitionRepository,
                                            EmailTemplateVariableValidator variableValidator,
                                            NotificationActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.variableValidator = variableValidator;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailTemplateResponse createTemplate(CreateEmailTemplateCommand cmd) {
        EmailTemplateScope scope = NotificationEnumParser.parseTemplateScope(cmd.scope());

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

    @Transactional
    public EmailTemplateResponse updateTemplate(UpdateEmailTemplateCommand cmd) {
        EmailTemplate template = findOrThrow(cmd.id());
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

    @Transactional
    public EmailTemplateResponse activateTemplate(UUID id) {
        EmailTemplate template = findOrThrow(id);
        template.activate();
        template = templateRepository.save(template);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE, id,
                NotificationActivityActions.ACTIVATE_EMAIL_TEMPLATE, null);
        return EmailTemplateResponse.from(template);
    }

    @Transactional
    public EmailTemplateResponse deactivateTemplate(UUID id) {
        EmailTemplate template = findOrThrow(id);
        template.deactivate();
        template = templateRepository.save(template);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE, id,
                NotificationActivityActions.DEACTIVATE_EMAIL_TEMPLATE, null);
        return EmailTemplateResponse.from(template);
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        EmailTemplate template = findOrThrow(id);
        template.softDelete();
        templateRepository.save(template);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE, id,
                NotificationActivityActions.SOFT_DELETE_EMAIL_TEMPLATE, null);
    }

    @Transactional
    public EmailTemplateVersionResponse createVersion(CreateEmailTemplateVersionCommand cmd) {
        EmailTemplate template = findOrThrow(cmd.templateId());
        if (template.status() == EmailTemplateStatus.DELETED) {
            throw NotificationExceptions.emailTemplateDeleted(cmd.templateId());
        }
        int nextNumber = templateRepository.countVersionsByTemplateId(cmd.templateId()) + 1;
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                cmd.templateId(), nextNumber,
                cmd.subjectTemplate(), cmd.htmlBodyTemplate(), cmd.textBodyTemplate());
        version = templateRepository.saveVersion(version);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE_VERSION, version.id(),
                NotificationActivityActions.CREATE_EMAIL_TEMPLATE_VERSION,
                "Version " + version.versionNumber() + " created for template " + template.code().value());
        return EmailTemplateVersionResponse.from(version);
    }

    @Transactional
    public EmailTemplateVersionResponse publishVersion(PublishEmailTemplateVersionCommand cmd) {
        EmailTemplate template = findOrThrow(cmd.templateId());
        EmailTemplateVersion version = templateRepository.findVersionById(cmd.versionId())
                .orElseThrow(() -> NotificationExceptions.emailTemplateVersionNotFound(cmd.versionId()));

        Set<String> allowedPaths = loadAllowedVariablePaths(template.eventDefinitionId());
        variableValidator.validate(version.subjectTemplate(), version.htmlBodyTemplate(),
                version.textBodyTemplate(), allowedPaths);

        version.publish();
        version = templateRepository.saveVersion(version);

        template.publishVersion(version.id());
        templateRepository.save(template);

        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_TEMPLATE_VERSION, version.id(),
                NotificationActivityActions.PUBLISH_EMAIL_TEMPLATE_VERSION,
                "Version " + version.versionNumber() + " published for template " + template.code().value());
        return EmailTemplateVersionResponse.from(version);
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getTemplate(UUID id) {
        return EmailTemplateResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EmailTemplateVersionResponse> getVersions(UUID templateId) {
        findOrThrow(templateId);
        return templateRepository.findVersionsByTemplateId(templateId)
                .stream().map(EmailTemplateVersionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailTemplateResponse> searchTemplates(SearchEmailTemplatesQuery query) {
        EmailTemplateScope scope = query.scope() != null
                ? NotificationEnumParser.parseTemplateScope(query.scope()) : null;
        EmailTemplateStatus status = query.status() != null
                ? NotificationEnumParser.parseTemplateStatus(query.status()) : null;

        EmailTemplateSearchCriteria criteria = new EmailTemplateSearchCriteria(
                query.keyword(), scope, status,
                query.workspaceId(), query.eventDefinitionId(),
                query.page(), query.size());

        List<EmailTemplateResponse> items = templateRepository.findAll(criteria)
                .stream().map(EmailTemplateResponse::from).toList();
        long total = templateRepository.countAll(criteria);
        int totalPages = query.size() == 0 ? 1 : (int) Math.ceil((double) total / query.size());
        return new PageResponse<>(items, query.page(), query.size(), total, totalPages,
                query.page() == 0, query.page() >= totalPages - 1);
    }

    public EmailTemplateVersionResponse getVersion(UUID versionId) {
        return EmailTemplateVersionResponse.from(
                templateRepository.findVersionById(versionId)
                        .orElseThrow(() -> NotificationExceptions.emailTemplateVersionNotFound(versionId)));
    }

    private EmailTemplate findOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailTemplateNotFound(id));
    }

    private Set<String> loadAllowedVariablePaths(UUID eventDefinitionId) {
        List<EventVariable> variables = eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefinitionId);
        return variables.stream().map(EventVariable::variablePath).collect(Collectors.toSet());
    }
}
