package com.company.scopery.modules.collaboration.agendaitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.AGENDA_ITEM) @Getter @Setter @NoArgsConstructor
public class MeetingAgendaItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(nullable=false) private String status;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(name="timebox_minutes") private Integer timeboxMinutes;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
