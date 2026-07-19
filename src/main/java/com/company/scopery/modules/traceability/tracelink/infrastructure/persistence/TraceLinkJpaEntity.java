package com.company.scopery.modules.traceability.tracelink.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.TRACE_LINK) @Getter @Setter @NoArgsConstructor
public class TraceLinkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="source_type", nullable=false) private String sourceType;
    @Column(name="source_id", nullable=false) private UUID sourceId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="link_type", nullable=false) private String linkType;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
