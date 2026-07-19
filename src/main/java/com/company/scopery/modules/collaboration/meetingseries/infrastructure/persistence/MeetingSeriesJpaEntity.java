package com.company.scopery.modules.collaboration.meetingseries.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.MEETING_SERIES) @Getter @Setter @NoArgsConstructor
public class MeetingSeriesJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    private String cadence;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(nullable=false) private String status;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
