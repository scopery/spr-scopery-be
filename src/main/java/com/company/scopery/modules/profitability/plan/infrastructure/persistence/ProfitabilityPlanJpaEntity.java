package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.PLAN)
@Getter
@Setter
@NoArgsConstructor
public class ProfitabilityPlanJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "profitability_profile_id", nullable = false)
    private UUID profitabilityProfileId;
    @Column(name = "plan_code", length = 150)
    private String planCode;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;
    @Column(nullable = false, length = 50)
    private String status;
    @Column(name = "current_version_id")
    private UUID currentVersionId;
    @Version
    private Integer version;
}
