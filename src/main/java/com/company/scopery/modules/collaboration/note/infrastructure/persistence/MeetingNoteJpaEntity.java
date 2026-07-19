package com.company.scopery.modules.collaboration.note.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.NOTE) @Getter @Setter @NoArgsConstructor
public class MeetingNoteJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(name="agenda_item_id") private UUID agendaItemId;
    @Column(name="note_type", nullable=false) private String noteType;
    @Column(nullable=false, columnDefinition="text") private String body;
    @Column(name="internal_only", nullable=false) private boolean internalOnly;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="source_ai_suggestion_id") private UUID sourceAiSuggestionId;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
