package com.company.scopery.modules.scope.criteria.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ScopeTableNames.CRITERIA) @Getter @Setter @NoArgsConstructor
public class AcceptanceCriteriaJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="deliverable_id", nullable=false) private UUID deliverableId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(nullable=false) private String type;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private boolean mandatory;
    @Column(nullable=false) private String status;
    @Column(name="waive_reason", columnDefinition="text") private String waiveReason;
    @Column(name="waived_by") private UUID waivedBy;
    @Column(name="waived_at") private Instant waivedAt;
    @Version private Integer version;
}
