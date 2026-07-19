package com.company.scopery.modules.project.scheduling.engine;

import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchedulingCapacityResolver {

    private static final BigDecimal DEFAULT_FOCUS = new BigDecimal("0.75");
    private static final BigDecimal DEFAULT_HOURS = new BigDecimal("8");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final UserCapacityProfileRepository profiles;
    private final WorkingCalendarRepository calendars;
    private final CalendarDayRuleRepository dayRules;
    private final CalendarExceptionRepository exceptions;
    private final ProjectResourceAllocationRepository allocations;
    private final WorkspaceMemberRepository members;

    public SchedulingCapacityResolver(UserCapacityProfileRepository profiles,
                                      WorkingCalendarRepository calendars,
                                      CalendarDayRuleRepository dayRules,
                                      CalendarExceptionRepository exceptions,
                                      ProjectResourceAllocationRepository allocations,
                                      WorkspaceMemberRepository members) {
        this.profiles = profiles;
        this.calendars = calendars;
        this.dayRules = dayRules;
        this.exceptions = exceptions;
        this.allocations = allocations;
        this.members = members;
    }

    public CapacityResolution resolve(UUID workspaceId, UUID projectId, UUID userId,
                                      LocalDate from, LocalDate to) {
        Optional<WorkspaceMember> member = members.findByWorkspaceIdAndUserId(workspaceId, userId);
        boolean active = member.filter(m -> members.isActiveMember(workspaceId, userId)).isPresent();

        Optional<UserCapacityProfile> profile = profiles.findActiveByUserId(userId);
        BigDecimal focus = profile.map(UserCapacityProfile::focusFactor).orElse(DEFAULT_FOCUS);
        BigDecimal defaultHours = profile.map(UserCapacityProfile::defaultDailyHours).orElse(DEFAULT_HOURS);
        UUID calendarId = profile.map(UserCapacityProfile::workingCalendarId)
                .orElseGet(() -> calendars.findDefaultActiveByWorkspaceId(workspaceId)
                        .map(c -> c.id())
                        .orElse(null));

        Map<DayOfWeek, CalendarDayRule> rules = new HashMap<>();
        Map<LocalDate, CalendarException> overrides = new HashMap<>();
        if (calendarId != null) {
            for (CalendarDayRule rule : dayRules.findByWorkingCalendarId(calendarId)) {
                rules.put(rule.dayOfWeek(), rule);
            }
            for (CalendarException exception : exceptions.findByWorkingCalendarIdAndDateRange(calendarId, from, to)) {
                overrides.put(exception.exceptionDate(), exception);
            }
        }

        List<ProjectResourceAllocation> projectAllocations = allocations
                .findActiveByUserIdAndDateRange(userId, from, to)
                .stream()
                .filter(a -> a.projectId().equals(projectId))
                .toList();

        Map<LocalDate, DayCapacity> days = new LinkedHashMap<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            CalendarException exception = overrides.get(date);
            CalendarDayRule rule = rules.get(date.getDayOfWeek());
            boolean weekend = date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY;

            boolean working;
            BigDecimal hours;
            if (exception != null) {
                working = exception.isWorkingDay();
                hours = exception.workingHours();
            } else if (rule != null) {
                working = rule.isWorkingDay();
                hours = rule.workingHours();
            } else {
                working = !weekend;
                hours = working ? defaultHours : BigDecimal.ZERO;
            }

            BigDecimal percent = allocationPercentOn(date, projectAllocations);
            BigDecimal allocated = working
                    ? hours.multiply(focus).multiply(percent).divide(HUNDRED, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            days.put(date, new DayCapacity(date, working, allocated, percent.signum() > 0));
        }

        return new CapacityResolution(
                member.map(WorkspaceMember::id).orElse(null),
                active,
                calendarId != null,
                !projectAllocations.isEmpty(),
                days);
    }

    /**
     * Sum ACTIVE project allocation % covering the given date.
     * Intentionally avoids stream/lambda capture of the for-loop date variable.
     */
    private static BigDecimal allocationPercentOn(LocalDate date, List<ProjectResourceAllocation> projectAllocations) {
        BigDecimal percent = BigDecimal.ZERO;
        for (ProjectResourceAllocation allocation : projectAllocations) {
            if (date.isBefore(allocation.startDate())) {
                continue;
            }
            if (allocation.endDate() != null && date.isAfter(allocation.endDate())) {
                continue;
            }
            percent = percent.add(allocation.allocationPercent());
        }
        return percent;
    }

    public record DayCapacity(
            LocalDate date,
            boolean workingDay,
            BigDecimal projectAllocatedHours,
            boolean allocationPresent
    ) {}

    public record CapacityResolution(
            UUID workspaceMemberId,
            boolean activeMember,
            boolean calendarPresent,
            boolean allocationPresent,
            Map<LocalDate, DayCapacity> days
    ) {}
}
