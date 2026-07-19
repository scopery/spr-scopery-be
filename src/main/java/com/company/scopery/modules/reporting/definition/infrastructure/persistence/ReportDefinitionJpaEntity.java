package com.company.scopery.modules.reporting.definition.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.reporting.shared.constant.ReportingTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;
@Entity @Table(name = ReportingTableNames.DEFINITION) @Getter @Setter @NoArgsConstructor
public class ReportDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String name;
    @Column(columnDefinition = "text") private String description;
    @Column(nullable = false) private String scope;
    @Column(name = "report_type", nullable = false) private String reportType;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "required_permissions_json", nullable = false, columnDefinition = "jsonb") private String requiredPermissionsJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "supported_formats_json", nullable = false, columnDefinition = "jsonb") private String supportedFormatsJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "default_filters_json", columnDefinition = "jsonb") private String defaultFiltersJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "sensitive_fields_json", columnDefinition = "jsonb") private String sensitiveFieldsJson;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
