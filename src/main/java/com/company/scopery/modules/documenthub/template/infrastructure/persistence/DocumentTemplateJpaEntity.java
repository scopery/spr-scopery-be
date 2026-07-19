package com.company.scopery.modules.documenthub.template.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = DocumentHubTableNames.TEMPLATE) @Getter @Setter @NoArgsConstructor
public class DocumentTemplateJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column private String category;
    @Column(nullable=false) private String status;
    @Column(name="template_mode", nullable=false) private String templateMode;
    @Column(name="current_version_id") private UUID currentVersionId;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
