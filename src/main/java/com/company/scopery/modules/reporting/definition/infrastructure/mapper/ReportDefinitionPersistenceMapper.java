package com.company.scopery.modules.reporting.definition.infrastructure.mapper;
import com.company.scopery.modules.reporting.definition.domain.enums.ReportDefinitionStatus;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.infrastructure.persistence.ReportDefinitionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReportDefinitionPersistenceMapper {
    public ReportDefinition toDomain(ReportDefinitionJpaEntity e) {
        return new ReportDefinition(e.getId(), e.getCode(), e.getName(), e.getDescription(), e.getScope(), e.getReportType(),
                e.getRequiredPermissionsJson(), e.getSupportedFormatsJson(), e.getDefaultFiltersJson(), e.getSensitiveFieldsJson(),
                ReportDefinitionStatus.valueOf(e.getStatus()), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReportDefinitionJpaEntity toJpaEntity(ReportDefinition d) {
        ReportDefinitionJpaEntity e = new ReportDefinitionJpaEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setScope(d.scope()); e.setReportType(d.reportType()); e.setRequiredPermissionsJson(d.requiredPermissionsJson());
        e.setSupportedFormatsJson(d.supportedFormatsJson()); e.setDefaultFiltersJson(d.defaultFiltersJson());
        e.setSensitiveFieldsJson(d.sensitiveFieldsJson()); e.setStatus(d.status().name());
        if (d.createdAt() != null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}
