package com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.persistence;

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
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = CapacityTableNames.CALENDAR_EXCEPTION,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_capacity_calendar_exception_calendar_date",
                        columnNames = {"working_calendar_id", "exception_date"}
                )
        },
        indexes = {
                @Index(name = "idx_capacity_calendar_exception_calendar", columnList = "working_calendar_id"),
                @Index(name = "idx_capacity_calendar_exception_date", columnList = "exception_date")
        }
)
public class CalendarExceptionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "working_calendar_id", nullable = false, updatable = false)
    private UUID workingCalendarId;

    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    @Column(name = "exception_type", nullable = false, length = 50)
    private String exceptionType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_working_day", nullable = false)
    private boolean isWorkingDay;

    @Column(name = "working_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal workingHours;
}
