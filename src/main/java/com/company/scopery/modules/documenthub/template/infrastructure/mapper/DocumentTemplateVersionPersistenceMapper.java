package com.company.scopery.modules.documenthub.template.infrastructure.mapper;

import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVersion;
import com.company.scopery.modules.documenthub.template.infrastructure.persistence.DocumentTemplateVersionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentTemplateVersionPersistenceMapper {

    public DocumentTemplateVersion toDomain(DocumentTemplateVersionJpaEntity e) {
        return new DocumentTemplateVersion(e.getId(), e.getTemplateId(), e.getVersionNumber(),
                e.getBodyTemplate(), e.getOutputFormat(), e.getStatus(), e.getAst());
    }

    public DocumentTemplateVersionJpaEntity toJpaEntity(DocumentTemplateVersion d) {
        DocumentTemplateVersionJpaEntity e = new DocumentTemplateVersionJpaEntity();
        e.setId(d.id());
        e.setTemplateId(d.templateId());
        e.setVersionNumber(d.versionNumber());
        e.setBodyTemplate(d.bodyTemplate());
        e.setOutputFormat(d.outputFormat());
        e.setStatus(d.status());
        e.setAst(d.ast());
        return e;
    }
}
