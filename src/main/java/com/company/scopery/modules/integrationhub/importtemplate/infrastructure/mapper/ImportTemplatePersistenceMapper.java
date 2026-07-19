package com.company.scopery.modules.integrationhub.importtemplate.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.importtemplate.domain.model.ImportTemplate;
import com.company.scopery.modules.integrationhub.importtemplate.infrastructure.persistence.ImportTemplateJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ImportTemplatePersistenceMapper {
    public ImportTemplateJpaEntity toJpaEntity(ImportTemplate d) {
        ImportTemplateJpaEntity e = new ImportTemplateJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setTemplateCode(d.templateCode()); e.setName(d.name());
        e.setTargetObjectType(d.targetObjectType()); e.setSourceFormat(d.sourceFormat()); e.setSchemaJson(d.schemaJson());
        e.setEnabled(d.enabled()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ImportTemplate toDomain(ImportTemplateJpaEntity e) {
        return new ImportTemplate(e.getId(), e.getWorkspaceId(), e.getTemplateCode(), e.getName(),
                e.getTargetObjectType(), e.getSourceFormat(), e.getSchemaJson(),
                e.getEnabled() != null && e.getEnabled(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
