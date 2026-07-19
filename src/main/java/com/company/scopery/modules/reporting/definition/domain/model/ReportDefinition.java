package com.company.scopery.modules.reporting.definition.domain.model;
import com.company.scopery.modules.reporting.definition.domain.enums.ReportDefinitionStatus;
import java.time.Instant; import java.util.UUID;
public record ReportDefinition(UUID id, String code, String name, String description, String scope, String reportType,
        String requiredPermissionsJson, String supportedFormatsJson, String defaultFiltersJson, String sensitiveFieldsJson,
        ReportDefinitionStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static ReportDefinition create(String code, String name, String description, String scope, String reportType,
                                          String requiredPermissionsJson, String supportedFormatsJson, String sensitiveFieldsJson) {
        return new ReportDefinition(UUID.randomUUID(), code, name, description, scope, reportType,
                requiredPermissionsJson, supportedFormatsJson, null, sensitiveFieldsJson,
                ReportDefinitionStatus.ACTIVE, 0, null, null);
    }
}
