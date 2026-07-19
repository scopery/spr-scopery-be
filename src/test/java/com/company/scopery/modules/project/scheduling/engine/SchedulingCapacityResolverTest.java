package com.company.scopery.modules.project.scheduling.engine;

import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingCapacityResolverTest {

    @Mock private UserCapacityProfileRepository profiles;
    @Mock private WorkingCalendarRepository calendars;
    @Mock private CalendarDayRuleRepository dayRules;
    @Mock private CalendarExceptionRepository exceptions;
    @Mock private ProjectResourceAllocationRepository allocations;
    @Mock private WorkspaceMemberRepository members;

    private SchedulingCapacityResolver resolver;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        resolver = new SchedulingCapacityResolver(
                profiles, calendars, dayRules, exceptions, allocations, members);
    }

    @Test
    void resolve_usesCorrectAllocationPercentPerLoopDate_withoutLambdaCapture() {
        LocalDate mon = LocalDate.of(2026, 8, 3); // Monday
        LocalDate tue = mon.plusDays(1);
        LocalDate wed = mon.plusDays(2);

        when(members.findByWorkspaceIdAndUserId(workspaceId, userId))
                .thenReturn(Optional.of(WorkspaceMember.create(workspaceId, userId)));
        when(members.isActiveMember(workspaceId, userId)).thenReturn(true);
        when(profiles.findActiveByUserId(userId)).thenReturn(Optional.empty());
        when(calendars.findDefaultActiveByWorkspaceId(workspaceId)).thenReturn(Optional.empty());

        // 50% only Mon–Tue; 25% only Wed — each loop day must see its own percent
        ProjectResourceAllocation early = ProjectResourceAllocation.create(
                workspaceId, projectId, memberId, userId,
                new BigDecimal("50"), AllocationType.PLANNED, mon, tue, null);
        ProjectResourceAllocation late = ProjectResourceAllocation.create(
                workspaceId, projectId, memberId, userId,
                new BigDecimal("25"), AllocationType.PLANNED, wed, wed, null);
        when(allocations.findActiveByUserIdAndDateRange(eq(userId), eq(mon), eq(wed)))
                .thenReturn(List.of(early, late));

        SchedulingCapacityResolver.CapacityResolution result =
                resolver.resolve(workspaceId, projectId, userId, mon, wed);

        // focused = 8 * 0.75 = 6; Mon/Tue allocated = 6 * 50% = 3.00; Wed = 6 * 25% = 1.50
        assertThat(result.days().get(mon).projectAllocatedHours()).isEqualByComparingTo("3.00");
        assertThat(result.days().get(tue).projectAllocatedHours()).isEqualByComparingTo("3.00");
        assertThat(result.days().get(wed).projectAllocatedHours()).isEqualByComparingTo("1.50");
        assertThat(result.allocationPresent()).isTrue();
    }
}
