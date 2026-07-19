package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=RaidTableNames.IMPACT) @Getter @Setter @NoArgsConstructor
public class DecisionImpactJpaEntity {
    @Id private UUID id;
    @Column(name="decision_id", nullable=false) private UUID decisionId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="scope_impact", columnDefinition="text") private String scopeImpact;
    @Column(name="schedule_impact_days") private Integer scheduleImpactDays;
    @Column(name="estimate_hours_impact") private BigDecimal estimateHoursImpact;
    @Column(name="cost_impact") private BigDecimal costImpact;
    @Column(name="revenue_impact") private BigDecimal revenueImpact;
    @Column(name="margin_impact") private BigDecimal marginImpact;
    @Column(name="risk_impact", columnDefinition="text") private String riskImpact;
    @Column(name="deliverable_impact", columnDefinition="text") private String deliverableImpact;
    @Column(name="acceptance_impact", columnDefinition="text") private String acceptanceImpact;
    private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
