package com.company.scopery.modules.collaboration.artifactlink.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.ARTIFACT_LINK) @Getter @Setter @NoArgsConstructor
public class MeetingArtifactLinkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(name="agenda_item_id") private UUID agendaItemId;
    @Column(name="note_id") private UUID noteId;
    @Column(name="action_item_id") private UUID actionItemId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="link_type", nullable=false) private String linkType;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
