package com.company.scopery.modules.scope.evidence.infrastructure.persistence;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ScopeTableNames.EVIDENCE) @Getter @Setter @NoArgsConstructor
public class AcceptanceEvidenceJpaEntity {
    @Id private UUID id;
    @Column(name="deliverable_id", nullable=false) private UUID deliverableId;
    @Column(name="acceptance_criteria_id") private UUID acceptanceCriteriaId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="evidence_type", nullable=false) private String evidenceType;
    @Column(nullable=false) private String title;
    @Column(name="content_text", columnDefinition="text") private String contentText;
    @Column(name="link_url") private String linkUrl;
    @Column(name="reference_id") private String referenceId;
    @Version private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
