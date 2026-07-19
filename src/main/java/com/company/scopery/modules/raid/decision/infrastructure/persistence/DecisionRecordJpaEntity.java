package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=RaidTableNames.DECISION) @Getter @Setter @NoArgsConstructor
public class DecisionRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    private String code;
    @Column(nullable=false) private String title;
    @Column(nullable=false) private String category;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String rationale;
    @Column(columnDefinition="text") private String outcome;
    @Column(name="decided_at") private Instant decidedAt;
    @Column(name="decided_by") private UUID decidedBy;
    @Column(name="superseded_by_decision_id") private UUID supersededByDecisionId;
    @Column(name="linked_change_request_id") private UUID linkedChangeRequestId;
    @Version private Integer version;
}
