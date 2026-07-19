package com.company.scopery.modules.ratecard.costrole.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.ratecard.shared.constant.RateCardTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = RateCardTableNames.COST_ROLE,
        indexes = {
                @Index(name = "idx_rate_cost_role_scope", columnList = "scope"),
                @Index(name = "idx_rate_cost_role_org", columnList = "organization_id"),
                @Index(name = "idx_rate_cost_role_workspace", columnList = "workspace_id"),
                @Index(name = "idx_rate_cost_role_status", columnList = "status"),
                @Index(name = "idx_rate_cost_role_code", columnList = "code")
        }
)
public class CostRoleJpaEntity extends AuditableJpaEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "code", nullable = false, length = 100)
    private String code;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "scope", nullable = false, length = 50)
    private String scope;
    @Column(name = "organization_id")
    private UUID organizationId;
    @Column(name = "workspace_id")
    private UUID workspaceId;
    @Column(name = "category", length = 100)
    private String category;
    @Column(name = "built_in", nullable = false)
    private boolean builtIn;
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "archived_by")
    private UUID archivedBy;
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
