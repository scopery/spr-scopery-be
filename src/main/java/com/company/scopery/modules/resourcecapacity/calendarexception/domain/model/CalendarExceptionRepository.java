package com.company.scopery.modules.resourcecapacity.calendarexception.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarExceptionRepository {

    CalendarException save(CalendarException exception);

    Optional<CalendarException> findById(UUID id);

    boolean existsByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate);

    Optional<CalendarException> findByWorkingCalendarIdAndExceptionDate(UUID workingCalendarId, LocalDate exceptionDate);

    List<CalendarException> findByWorkingCalendarIdAndDateRange(UUID workingCalendarId, LocalDate from, LocalDate to);

    PageResult<CalendarException> search(UUID workingCalendarId, LocalDate from, LocalDate to, PageQuery pageQuery);

    void deleteById(UUID id);
}
