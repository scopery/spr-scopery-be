package com.company.scopery.modules.resourcecapacity.dayrule.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.resourcecapacity.dayrule.application.command.DayRuleItem;
import com.company.scopery.modules.resourcecapacity.dayrule.application.command.ReplaceDayRulesCommand;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityDayRuleBusinessRulesActionTest {

    @Mock private CalendarDayRuleRepository dayRuleRepository;
    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityActivityLogger activityLogger;

    private ReplaceDayRulesAction replaceDayRulesAction;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        replaceDayRulesAction = new ReplaceDayRulesAction(
                dayRuleRepository, workingCalendarRepository, authorizationService, activityLogger);
    }

    @Test
    void updateDayRules_valid_success() {
        mockActiveCalendar();
        when(dayRuleRepository.replaceAll(eq(calendarId), any())).thenAnswer(inv -> inv.getArgument(1));

        var response = replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, validWeek()));

        assertThat(response).hasSize(7);
        assertThat(response).filteredOn(r -> r.isWorkingDay()).hasSize(5);
    }

    @Test
    void updateDayRules_duplicateDay_rejected() {
        mockActiveCalendar();

        List<DayRuleItem> items = new ArrayList<>(validWeek());
        items.set(6, workingDay(DayOfWeek.MONDAY, new BigDecimal("8")));

        assertThatThrownBy(() -> replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_DAY_RULE_DUPLICATE.code()));
    }

    @Test
    void updateDayRules_workingDayZeroHours_rejected() {
        mockActiveCalendar();

        List<DayRuleItem> items = new ArrayList<>(validWeek());
        items.set(0, workingDay(DayOfWeek.MONDAY, BigDecimal.ZERO));

        assertThatThrownBy(() -> replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_DAY_RULE_INVALID_HOURS.code()));
    }

    @Test
    void updateDayRules_nonWorkingDayNonZeroHours_rejected() {
        mockActiveCalendar();

        List<DayRuleItem> items = new ArrayList<>(validWeek());
        items.set(5, nonWorkingDay(DayOfWeek.SATURDAY, new BigDecimal("2")));

        assertThatThrownBy(() -> replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_DAY_RULE_INVALID_HOURS.code()));
    }

    @Test
    void updateDayRules_hoursOver24_rejected() {
        mockActiveCalendar();

        List<DayRuleItem> items = new ArrayList<>(validWeek());
        items.set(0, workingDay(DayOfWeek.MONDAY, new BigDecimal("25")));

        assertThatThrownBy(() -> replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_DAY_RULE_INVALID_HOURS.code()));
    }

    @Test
    void updateDayRules_noWorkingDay_rejected() {
        mockActiveCalendar();

        List<DayRuleItem> items = List.of(
                nonWorkingDay(DayOfWeek.MONDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.TUESDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.WEDNESDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.THURSDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.FRIDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.SATURDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.SUNDAY, BigDecimal.ZERO)
        );

        assertThatThrownBy(() -> replaceDayRulesAction.execute(new ReplaceDayRulesCommand(calendarId, items)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.CAPACITY_CALENDAR_NO_WORKING_DAY.code()));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void mockActiveCalendar() {
        Instant now = Instant.now();
        WorkingCalendar calendar = new WorkingCalendar(calendarId, workspaceId, "CAL_01", "Calendar", null,
                "UTC", false, WorkingCalendarStatus.ACTIVE, null, null, 0, now, now);
        when(workingCalendarRepository.findById(calendarId)).thenReturn(Optional.of(calendar));
    }

    private List<DayRuleItem> validWeek() {
        return List.of(
                workingDay(DayOfWeek.MONDAY, new BigDecimal("8")),
                workingDay(DayOfWeek.TUESDAY, new BigDecimal("8")),
                workingDay(DayOfWeek.WEDNESDAY, new BigDecimal("8")),
                workingDay(DayOfWeek.THURSDAY, new BigDecimal("8")),
                workingDay(DayOfWeek.FRIDAY, new BigDecimal("8")),
                nonWorkingDay(DayOfWeek.SATURDAY, BigDecimal.ZERO),
                nonWorkingDay(DayOfWeek.SUNDAY, BigDecimal.ZERO)
        );
    }

    private DayRuleItem workingDay(DayOfWeek day, BigDecimal hours) {
        return new DayRuleItem(day, true, LocalTime.of(9, 0), LocalTime.of(17, 0), hours);
    }

    private DayRuleItem nonWorkingDay(DayOfWeek day, BigDecimal hours) {
        return new DayRuleItem(day, false, null, null, hours);
    }
}
