package com.company.scopery.modules.resourcecapacity.dayrule.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = CapacityTableNames.CALENDAR_DAY_RULE,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_capacity_calendar_day_rule_calendar_day",
                        columnNames = {"working_calendar_id", "day_of_week"}
                )
        },
        indexes = {
                @Index(name = "idx_capacity_calendar_day_rule_calendar", columnList = "working_calendar_id")
        }
)
public class CalendarDayRuleJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "working_calendar_id", nullable = false, updatable = false)
    private UUID workingCalendarId;

    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "is_working_day", nullable = false)
    private boolean isWorkingDay;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "working_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal workingHours;
}
