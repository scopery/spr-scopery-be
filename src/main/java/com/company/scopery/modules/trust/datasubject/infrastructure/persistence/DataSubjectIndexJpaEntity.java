package com.company.scopery.modules.trust.datasubject.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.DATA_SUBJECT_INDEX) @Getter @Setter @NoArgsConstructor
public class DataSubjectIndexJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "subject_type", length = 50, nullable = false) private String subjectType;
    @Column(name = "subject_id") private UUID subjectId;
    @Column(name = "display_name_snapshot", length = 255) private String displayNameSnapshot;
    @Column(name = "linked_user_id") private UUID linkedUserId;
    @Column(name = "linked_external_contact_id") private UUID linkedExternalContactId;
    @Column(name = "record_count", nullable = false) private long recordCount;
    @Column(name = "last_rebuilt_at") private Instant lastRebuiltAt;
    @Column(length = 50, nullable = false) private String status;
    @Version private Integer version;
}
