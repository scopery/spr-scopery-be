package com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = CapacityTableNames.PROJECT_RESOURCE_ALLOCATION,
        indexes = {
                @Index(name = "idx_capacity_project_allocation_workspace", columnList = "workspace_id"),
                @Index(name = "idx_capacity_project_allocation_project", columnList = "project_id"),
                @Index(name = "idx_capacity_project_allocation_member", columnList = "workspace_member_id"),
                @Index(name = "idx_capacity_project_allocation_user", columnList = "user_id"),
                @Index(name = "idx_capacity_project_allocation_status", columnList = "status"),
                @Index(name = "idx_capacity_project_allocation_dates", columnList = "start_date, end_date")
        }
)
public class ProjectResourceAllocationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "workspace_member_id", nullable = false)
    private UUID workspaceMemberId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "allocation_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal allocationPercent;

    @Column(name = "allocation_type", nullable = false, length = 50)
    private String allocationType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
