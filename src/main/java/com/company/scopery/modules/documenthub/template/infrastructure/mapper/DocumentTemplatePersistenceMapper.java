package com.company.scopery.modules.documenthub.template.infrastructure.mapper;
import com.company.scopery.modules.documenthub.template.domain.enums.DocumentTemplateStatus;
import com.company.scopery.modules.documenthub.template.domain.enums.TemplateMode;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplate;
import com.company.scopery.modules.documenthub.template.infrastructure.persistence.DocumentTemplateJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DocumentTemplatePersistenceMapper {
    public DocumentTemplate toDomain(DocumentTemplateJpaEntity e) {
        TemplateMode mode = e.getTemplateMode() != null ? TemplateMode.valueOf(e.getTemplateMode()) : TemplateMode.FILE;
        return new DocumentTemplate(e.getId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getDescription(), e.getCategory(),
                DocumentTemplateStatus.valueOf(e.getStatus()), mode, e.getCurrentVersionId(), e.getArchivedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DocumentTemplateJpaEntity toJpaEntity(DocumentTemplate d) {
        DocumentTemplateJpaEntity e = new DocumentTemplateJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setName(d.name());
        e.setDescription(d.description()); e.setCategory(d.category()); e.setStatus(d.status().name());
        e.setTemplateMode(d.templateMode() != null ? d.templateMode().name() : TemplateMode.FILE.name());
        e.setCurrentVersionId(d.currentVersionId()); e.setArchivedAt(d.archivedAt());
        if (d.createdAt()!=null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}
