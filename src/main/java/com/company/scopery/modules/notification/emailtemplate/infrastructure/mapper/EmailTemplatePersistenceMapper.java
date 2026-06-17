package com.company.scopery.modules.notification.emailtemplate.infrastructure.mapper;

import com.company.scopery.modules.notification.emailtemplate.domain.*;
import com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence.EmailTemplateJpaEntity;
import com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence.EmailTemplateVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplatePersistenceMapper {

    public EmailTemplate toDomain(EmailTemplateJpaEntity e) {
        return EmailTemplate.reconstitute(
                e.getId(), EmailTemplateCode.of(e.getCode()),
                e.getName(), e.getDescription(),
                EmailTemplateScope.valueOf(e.getScope()),
                e.getWorkspaceId(), e.getEventDefinitionId(),
                EmailTemplateStatus.valueOf(e.getStatus()),
                e.getCurrentVersionId(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }

    public EmailTemplateJpaEntity toJpaEntity(EmailTemplate d) {
        EmailTemplateJpaEntity e = new EmailTemplateJpaEntity();
        e.setId(d.id());
        e.setCode(d.code().value());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setScope(d.scope().name());
        e.setWorkspaceId(d.workspaceId());
        e.setEventDefinitionId(d.eventDefinitionId());
        e.setStatus(d.status().name());
        e.setCurrentVersionId(d.currentVersionId());
        e.setDeletedAt(d.deletedAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public EmailTemplateVersion toVersionDomain(EmailTemplateVersionJpaEntity e) {
        return EmailTemplateVersion.reconstitute(
                e.getId(), e.getTemplateId(), e.getVersionNumber(),
                e.getSubjectTemplate(), e.getHtmlBodyTemplate(), e.getTextBodyTemplate(),
                EmailTemplateVersionStatus.valueOf(e.getStatus()),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public EmailTemplateVersionJpaEntity toVersionJpaEntity(EmailTemplateVersion d) {
        EmailTemplateVersionJpaEntity e = new EmailTemplateVersionJpaEntity();
        e.setId(d.id());
        e.setTemplateId(d.templateId());
        e.setVersionNumber(d.versionNumber());
        e.setSubjectTemplate(d.subjectTemplate());
        e.setHtmlBodyTemplate(d.htmlBodyTemplate());
        e.setTextBodyTemplate(d.textBodyTemplate());
        e.setStatus(d.status().name());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
