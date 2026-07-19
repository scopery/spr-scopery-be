package com.company.scopery.modules.integrationhub.importtemplate.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.IMPORT_TEMPLATE) @Getter @Setter @NoArgsConstructor
public class ImportTemplateJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id") private UUID workspaceId;
    @Column(name="template_code", nullable=false) private String templateCode;
    @Column(nullable=false) private String name;
    @Column(name="target_object_type", nullable=false) private String targetObjectType;
    @Column(name="source_format", nullable=false) private String sourceFormat;
    @Column(name="schema_json", nullable=false, columnDefinition="jsonb") private String schemaJson;
    @Column(nullable=false) private Boolean enabled = true;
    @Version private Integer version;
}
