package com.company.scopery.modules.trust.evidence.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = TrustTableNames.COMPLIANCE_EVIDENCE) @Getter @Setter @NoArgsConstructor
public class ComplianceEvidenceRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="evidence_code") private String evidenceCode;
    @Column(name="evidence_type", nullable=false) private String evidenceType;
    @Column(nullable=false) private String title;
    private String description;
    @Column(nullable=false) private String status;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(name="evidence_date", nullable=false) private LocalDate evidenceDate;
    @Column(name="finalized_at") private Instant finalizedAt;
    @Column(name="finalized_by") private UUID finalizedBy;
    @Version private Integer version;
}
