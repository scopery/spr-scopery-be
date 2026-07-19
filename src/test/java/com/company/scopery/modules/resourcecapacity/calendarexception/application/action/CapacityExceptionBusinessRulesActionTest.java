package com.company.scopery.modules.resourcecapacity.calendarexception.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.CreateCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.DeleteCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums.CalendarExceptionType;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityExceptionBusinessRulesActionTest {

    @Mock private CalendarExceptionRepository calendarExceptionRepository;
    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CapacityActivityLogger activityLogger;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityPlatformPublisher platformPublisher;

    private CreateCalendarExceptionAction createCalendarExceptionAction;
    private DeleteCalendarExceptionAction deleteCalendarExceptionAction;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();
    private final LocalDate exceptionDate = LocalDate.of(2026, 12, 25);

    @BeforeEach
    void setUp() {
        createCalendarExceptionAction = new CreateCalendarExceptionAction(
                calendarExceptionRepository, workingCalendarRepository, activityLogger,
                authorizationService, platformPublisher);
        deleteCalendarExceptionAction = new DeleteCalendarExceptionAction(
                calendarExceptionRepository, workingCalendarRepository, activityLogger,
                authorizationService, platformPublisher);
    }

    @Test
    void createException_holiday_success() {
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(activeCalendar()));
        when(calendarExceptionRepository.existsByWorkingCalendarIdAndExceptionDate(calendarId, exceptionDate))
                .thenReturn(false);
        when(calendarExceptionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = createCalendarExceptionAction.execute(new CreateCalendarExceptionCommand(
                calendarId, exceptionDate, "HOLIDAY", "Christmas", null, false, BigDecimal.ZERO));

        assertThat(response.exceptionType()).isEqualTo("HOLIDAY");
        assertThat(response.isWorkingDay()).isFalse();
        assertThat(response.workingHours()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(platformPublisher).enqueueException(any(), eq("CAPACITY_EXCEPTION_CREATED"));
    }

    @Test
    void createException_duplicateDate_rejected() {
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(activeCalendar()));
        when(calendarExceptionRepository.existsByWorkingCalendarIdAndExceptionDate(calendarId, exceptionDate))
                .thenReturn(true);

        assertThatThrownBy(() -> createCalendarExceptionAction.execute(new CreateCalendarExceptionCommand(
                calendarId, exceptionDate, "HOLIDAY", "Christmas", null, false, BigDecimal.ZERO)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(CapacityErrorCatalog.CAPACITY_EXCEPTION_DUPLICATE_DATE.code());
                });
    }

    @Test
    void createException_invalidHours_rejected() {
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(activeCalendar()));

        assertThatThrownBy(() -> createCalendarExceptionAction.execute(new CreateCalendarExceptionCommand(
                calendarId, exceptionDate, "ADJUSTED_HOURS", "Adjusted", null, true, new BigDecimal("30"))))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_EXCEPTION_INVALID_HOURS.code()));
    }

    @Test
    void deleteException_success() {
        UUID exceptionId = UUID.randomUUID();
        CalendarException exception = new CalendarException(exceptionId, calendarId, exceptionDate,
                CalendarExceptionType.HOLIDAY, "Christmas", null, false, BigDecimal.ZERO, Instant.now(), Instant.now());

        when(calendarExceptionRepository.findById(exceptionId)).thenReturn(Optional.of(exception));
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(activeCalendar()));

        deleteCalendarExceptionAction.execute(new DeleteCalendarExceptionCommand(exceptionId, calendarId));

        verify(calendarExceptionRepository).deleteById(exceptionId);
        verify(platformPublisher).enqueueException(any(), eq("CAPACITY_EXCEPTION_DELETED"));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private WorkingCalendar activeCalendar() {
        Instant now = Instant.now();
        return new WorkingCalendar(calendarId, workspaceId, "CAL_01", "Calendar", null, "UTC",
                false, WorkingCalendarStatus.ACTIVE, null, null, 0, now, now);
    }
}
