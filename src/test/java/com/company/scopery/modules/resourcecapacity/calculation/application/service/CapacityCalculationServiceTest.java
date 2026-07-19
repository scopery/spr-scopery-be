package com.company.scopery.modules.resourcecapacity.calculation.application.service;

import com.company.scopery.modules.resourcecapacity.calculation.application.query.CalculateCapacityQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.CapacityCalculationResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.enums.CalendarExceptionType;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityCalculationServiceTest {

    @Mock private UserCapacityProfileRepository profileRepository;
    @Mock private WorkingCalendarRepository workingCalendarRepository;
    @Mock private CalendarDayRuleRepository dayRuleRepository;
    @Mock private CalendarExceptionRepository exceptionRepository;
    @Mock private ProjectResourceAllocationRepository allocationRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;

    private CapacityCalculationService calculationService;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();

    // July 13, 2026 is a Monday; July 18, 2026 is a Saturday.
    private final LocalDate monday = LocalDate.of(2026, 7, 13);
    private final LocalDate saturday = LocalDate.of(2026, 7, 18);

    @BeforeEach
    void setUp() {
        calculationService = new CapacityCalculationService(
                profileRepository, workingCalendarRepository, dayRuleRepository, exceptionRepository,
                allocationRepository, workspaceMemberRepository, authorizationService);

        lenient().when(profileRepository.findActiveByUserId(any())).thenReturn(Optional.empty());
        lenient().when(workingCalendarRepository.findDefaultActiveByWorkspaceId(any())).thenReturn(Optional.empty());
        lenient().when(exceptionRepository.findByWorkingCalendarIdAndDateRange(any(), any(), any())).thenReturn(List.of());
        lenient().when(dayRuleRepository.findByWorkingCalendarId(any())).thenReturn(List.of());
        lenient().when(allocationRepository.findActiveByUserIdAndDateRange(any(), any(), any())).thenReturn(List.of());
    }

    @Test
    void calculateCapacity_weekday_success() {
        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, null, monday, monday));

        assertThat(response.dailyEntries()).hasSize(1);
        assertThat(response.dailyEntries().get(0).isWorkingDay()).isTrue();
        assertThat(response.dailyEntries().get(0).workingHours()).isEqualByComparingTo(new BigDecimal("8"));
        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(response.totalFocusedHours()).isEqualByComparingTo(new BigDecimal("6.00"));
    }

    @Test
    void calculateCapacity_nonWorkingDay_zero() {
        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, null, saturday, saturday));

        assertThat(response.dailyEntries()).hasSize(1);
        assertThat(response.dailyEntries().get(0).isWorkingDay()).isFalse();
        assertThat(response.dailyEntries().get(0).workingHours()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    void calculateCapacity_holiday_zero() {
        UserCapacityProfile profile = UserCapacityProfile.create(
                workspaceId, UUID.randomUUID(), userId, calendarId, new BigDecimal("8"), new BigDecimal("0.75"),
                LocalDate.of(2026, 1, 1), null);
        when(profileRepository.findActiveByUserId(userId)).thenReturn(Optional.of(profile));

        CalendarException holiday = CalendarException.create(
                calendarId, monday, CalendarExceptionType.HOLIDAY, "Company Holiday", null, false, BigDecimal.ZERO);
        when(exceptionRepository.findByWorkingCalendarIdAndDateRange(calendarId, monday, monday))
                .thenReturn(List.of(holiday));

        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, null, monday, monday));

        assertThat(response.dailyEntries().get(0).isWorkingDay()).isFalse();
        assertThat(response.dailyEntries().get(0).workingHours()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    void calculateCapacity_focusFactorApplied() {
        UserCapacityProfile profile = UserCapacityProfile.create(
                workspaceId, UUID.randomUUID(), userId, calendarId, new BigDecimal("8"), new BigDecimal("0.5"),
                LocalDate.of(2026, 1, 1), null);
        when(profileRepository.findActiveByUserId(userId)).thenReturn(Optional.of(profile));

        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, null, monday, monday));

        assertThat(response.dailyEntries().get(0).workingHours()).isEqualByComparingTo(new BigDecimal("8"));
        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void calculateCapacity_allocationPercentApplied() {
        ProjectResourceAllocation allocation = ProjectResourceAllocation.create(
                workspaceId, projectId, UUID.randomUUID(), userId, new BigDecimal("50"),
                AllocationType.CONFIRMED, monday, monday, null);
        when(allocationRepository.findActiveByUserIdAndDateRange(userId, monday, monday)).thenReturn(List.of(allocation));

        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, projectId, monday, monday));

        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(response.dailyEntries().get(0).projectAllocatedHours()).isEqualByComparingTo(new BigDecimal("3.00"));
        assertThat(response.totalProjectAllocatedHours()).isEqualByComparingTo(new BigDecimal("3.00"));
    }

    @Test
    void calculateCapacity_missingProfile_usesDefault() {
        WorkingCalendar defaultCalendar = new WorkingCalendar(calendarId, workspaceId, "CAL_DEFAULT", "Default",
                null, "UTC", true, WorkingCalendarStatus.ACTIVE, null, null, 0, null, null);
        when(profileRepository.findActiveByUserId(userId)).thenReturn(Optional.empty());
        when(workingCalendarRepository.findDefaultActiveByWorkspaceId(workspaceId)).thenReturn(Optional.of(defaultCalendar));

        CapacityCalculationResponse response = calculationService.calculate(
                new CalculateCapacityQuery(workspaceId, userId, null, monday, monday));

        assertThat(response.dailyEntries().get(0).workingHours()).isEqualByComparingTo(new BigDecimal("8"));
        assertThat(response.dailyEntries().get(0).focusedHours()).isEqualByComparingTo(new BigDecimal("6.00"));
        verify(workingCalendarRepository).findDefaultActiveByWorkspaceId(workspaceId);
    }

    @Test
    void calculateCapacity_doesNotReturnEstimatedFinishDate() {
        List<String> componentNames = java.util.Arrays.stream(CapacityCalculationResponse.class.getRecordComponents())
                .map(RecordComponent::getName)
                .toList();

        assertThat(componentNames).doesNotContain("estimatedFinishDate");
        assertThat(componentNames).containsExactly(
                "workspaceId", "userId", "projectId", "fromDate", "toDate",
                "dailyEntries", "totalWorkingHours", "totalFocusedHours", "totalProjectAllocatedHours");
    }
}
