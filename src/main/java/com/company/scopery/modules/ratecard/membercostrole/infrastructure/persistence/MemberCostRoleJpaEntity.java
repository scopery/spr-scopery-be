package com.company.scopery.modules.ratecard.membercostrole.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.ratecard.shared.constant.RateCardTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = RateCardTableNames.MEMBER_COST_ROLE, indexes = {
        @Index(name = "idx_rate_workspace_member_cost_role_workspace", columnList = "workspace_id"),
        @Index(name = "idx_rate_workspace_member_cost_role_member", columnList = "workspace_member_id"),
        @Index(name = "idx_rate_workspace_member_cost_role_status", columnList = "status")
})
public class MemberCostRoleJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "workspace_id", nullable = false, updatable = false) private UUID workspaceId;
    @Column(name = "workspace_member_id", nullable = false) private UUID workspaceMemberId;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "cost_role_id", nullable = false) private UUID costRoleId;
    @Column(name = "is_default", nullable = false) private boolean isDefault;
    @Column(name = "effective_from", nullable = false) private LocalDate effectiveFrom;
    @Column(name = "effective_to") private LocalDate effectiveTo;
    @Column(name = "status", nullable = false, length = 50) private String status;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Version @Column(name = "version", nullable = false) private Integer version;
}
