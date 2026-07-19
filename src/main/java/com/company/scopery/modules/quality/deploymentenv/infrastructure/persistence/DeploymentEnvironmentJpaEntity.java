package com.company.scopery.modules.quality.deploymentenv.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.DEPLOYMENT_ENVIRONMENT) @Getter @Setter @NoArgsConstructor
public class DeploymentEnvironmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(name="environment_type", nullable=false) private String environmentType;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private Boolean active;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
