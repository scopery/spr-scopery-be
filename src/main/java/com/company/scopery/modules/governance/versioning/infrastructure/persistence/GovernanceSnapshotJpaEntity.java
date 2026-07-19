package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.SNAPSHOT) @Getter @Setter @NoArgsConstructor
public class GovernanceSnapshotJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="snapshot_mode") private String snapshotMode;
    @Column(name="schema_version") private String schemaVersion;
    @Column(name="snapshot_json", nullable=false) private String snapshotJson;
    @Column(name="masked_fields_json") private String maskedFieldsJson;
    @Column(name="sensitive_fields_present", nullable=false) private boolean sensitiveFieldsPresent;
    @Version private Integer version;
}
