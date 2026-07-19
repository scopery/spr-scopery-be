package com.company.scopery.modules.collaboration.meeting.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.MEETING) @Getter @Setter @NoArgsConstructor
public class MeetingJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_series_id") private UUID meetingSeriesId;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="meeting_type", nullable=false) private String meetingType;
    @Column(nullable=false) private String status;
    @Column(name="start_at", nullable=false) private Instant startAt;
    @Column(name="end_at") private Instant endAt;
    private String timezone; private String location;
    @Column(name="meeting_url") private String meetingUrl;
    @Column(name="organizer_user_id") private UUID organizerUserId;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(name="internal_only", nullable=false) private boolean internalOnly;
    @Column(name="cancelled_at") private Instant cancelledAt;
    @Column(name="cancelled_by") private UUID cancelledBy;
    @Column(name="cancel_reason", columnDefinition="text") private String cancelReason;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
