package com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity @Table(name = ProjectBaselineTableNames.CHANGE_REQUEST_ITEM)
@Getter @Setter @NoArgsConstructor
public class ChangeRequestItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="change_request_id", nullable=false) private UUID changeRequestId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id") private UUID targetId;
    @Column(nullable=false) private String operation;
    @Column(nullable=false, length=500) private String summary;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="before_snapshot_json", columnDefinition="jsonb") private String beforeSnapshotJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="after_snapshot_json", columnDefinition="jsonb") private String afterSnapshotJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="apply_payload_json", columnDefinition="jsonb") private String applyPayloadJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
