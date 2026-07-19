package com.company.scopery.modules.resourcecapacity.calendarexception.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCalendarExceptionJpaRepository
        extends JpaRepository<CalendarExceptionJpaEntity, UUID>, JpaSpecificationExecutor<CalendarExceptionJpaEntity> {

    boolean existsByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate);

    Optional<CalendarExceptionJpaEntity> findByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate);

    List<CalendarExceptionJpaEntity> findByWorkingCalendarIdAndExceptionDateBetween(
            UUID workingCalendarId, LocalDate from, LocalDate to);
}
