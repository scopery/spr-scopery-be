package com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.persistence;

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
        name = CapacityTableNames.USER_PROFILE,
        indexes = {
                @Index(name = "idx_capacity_user_profile_workspace", columnList = "workspace_id"),
                @Index(name = "idx_capacity_user_profile_member", columnList = "workspace_member_id"),
                @Index(name = "idx_capacity_user_profile_user", columnList = "user_id"),
                @Index(name = "idx_capacity_user_profile_status", columnList = "capacity_status"),
                @Index(name = "idx_capacity_user_profile_calendar", columnList = "working_calendar_id")
        }
)
public class UserCapacityProfileJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "workspace_member_id", nullable = false, updatable = false)
    private UUID workspaceMemberId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "working_calendar_id", nullable = false)
    private UUID workingCalendarId;

    @Column(name = "default_daily_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultDailyHours;

    @Column(name = "focus_factor", nullable = false, precision = 4, scale = 3)
    private BigDecimal focusFactor;

    @Column(name = "capacity_status", nullable = false, length = 50)
    private String capacityStatus;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
