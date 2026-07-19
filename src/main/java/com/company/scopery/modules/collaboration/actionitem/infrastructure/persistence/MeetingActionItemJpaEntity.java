package com.company.scopery.modules.collaboration.actionitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.ACTION_ITEM) @Getter @Setter @NoArgsConstructor
public class MeetingActionItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(name="agenda_item_id") private UUID agendaItemId;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="owner_target_type") private String ownerTargetType;
    @Column(name="owner_target_id") private UUID ownerTargetId;
    @Column(name="due_date") private LocalDate dueDate;
    @Column(nullable=false) private String status;
    @Column(name="linked_task_id") private UUID linkedTaskId;
    @Column(name="linked_raid_action_id") private UUID linkedRaidActionId;
    @Column(name="completed_at") private Instant completedAt; @Column(name="completed_by") private UUID completedBy;
    @Column(name="completion_note", columnDefinition="text") private String completionNote;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
