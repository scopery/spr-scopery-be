package com.company.scopery.modules.collaboration.participant.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.PARTICIPANT) @Getter @Setter @NoArgsConstructor
public class MeetingParticipantJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id") private UUID targetId;
    @Column(name="display_name_snapshot") private String displayNameSnapshot;
    @Column(name="email_snapshot") private String emailSnapshot;
    @Column(name="participant_role", nullable=false) private String participantRole;
    @Column(name="attendance_status", nullable=false) private String attendanceStatus;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Version private Integer version;
}
