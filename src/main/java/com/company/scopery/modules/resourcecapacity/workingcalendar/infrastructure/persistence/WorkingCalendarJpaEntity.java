package com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
        name = CapacityTableNames.WORKING_CALENDAR,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_capacity_working_calendar_workspace_code",
                        columnNames = {"workspace_id", "code"}
                )
        },
        indexes = {
                @Index(name = "idx_capacity_working_calendar_workspace", columnList = "workspace_id"),
                @Index(name = "idx_capacity_working_calendar_status", columnList = "status")
        }
)
public class WorkingCalendarJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "timezone", nullable = false, length = 100)
    private String timezone;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

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
