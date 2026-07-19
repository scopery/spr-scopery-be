package com.company.scopery.modules.resourcecapacity.calculation.application.service;

import com.company.scopery.modules.resourcecapacity.calculation.application.query.CalculateCapacityQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.OverAllocationQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.ProjectAllocationSummaryQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.UserAvailabilityQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.WorkspaceOverviewQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.AllocationSummaryItem;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.CapacityCalculationResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.DailyCapacityEntry;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.OverAllocatedUser;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.OverAllocationResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.ProjectAllocationSummaryResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.UserAvailabilityResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.UserCapacitySummary;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.WorkspaceOverviewResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarException;
import com.company.scopery.modules.resourcecapacity.calendarexception.domain.model.CalendarExceptionRepository;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRule;
import com.company.scopery.modules.resourcecapacity.dayrule.domain.model.CalendarDayRuleRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfileRepository;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendarRepository;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CapacityCalculationService {

    private static final BigDecimal DEFAULT_FOCUS_FACTOR = new BigDecimal("0.75");
    private static final BigDecimal DEFAULT_DAILY_HOURS = new BigDecimal("8");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final UserCapacityProfileRepository profileRepository;
    private final WorkingCalendarRepository workingCalendarRepository;
    private final CalendarDayRuleRepository dayRuleRepository;
    private final CalendarExceptionRepository exceptionRepository;
    private final ProjectResourceAllocationRepository allocationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public CapacityCalculationService(UserCapacityProfileRepository profileRepository,
                                      WorkingCalendarRepository workingCalendarRepository,
                                      CalendarDayRuleRepository dayRuleRepository,
                                      CalendarExceptionRepository exceptionRepository,
                                      ProjectResourceAllocationRepository allocationRepository,
                                      WorkspaceMemberRepository workspaceMemberRepository,
                                      CapacityWorkspaceAuthorizationService authorizationService) {
        this.profileRepository = profileRepository;
        this.workingCalendarRepository = workingCalendarRepository;
        this.dayRuleRepository = dayRuleRepository;
        this.exceptionRepository = exceptionRepository;
        this.allocationRepository = allocationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public UserAvailabilityResponse getUserAvailability(UserAvailabilityQuery query) {
        authorizationService.requireCapacityView(query.workspaceId());
        validateRange(query.fromDate(), query.toDate());

        UserCapacityContext context = resolveContext(query.workspaceId(), query.userId());
        List<DailyCapacityEntry> entries = computeDailyEntries(
                context, query.fromDate(), query.toDate(), null);

        BigDecimal totalWorking = sum(entries, DailyCapacityEntry::workingHours);
        BigDecimal totalFocused = sum(entries, DailyCapacityEntry::focusedHours);

        return new UserAvailabilityResponse(
                query.workspaceId(), query.userId(), query.fromDate(), query.toDate(),
                context.focusFactor(), context.defaultDailyHours(), context.usingLazyDefaults(),
                entries, totalWorking, totalFocused);
    }

    @Transactional(readOnly = true)
    public WorkspaceOverviewResponse getWorkspaceOverview(WorkspaceOverviewQuery query) {
        authorizationService.requireCapacityView(query.workspaceId());
        validateRange(query.fromDate(), query.toDate());

        List<WorkspaceMember> activeMembers = findAllActiveMembers(query.workspaceId());

        List<UserCapacitySummary> summaries = new ArrayList<>();
        BigDecimal workspaceFocusedTotal = BigDecimal.ZERO;
        int overAllocatedCount = 0;

        for (WorkspaceMember member : activeMembers) {
            UserCapacityContext context = resolveContext(query.workspaceId(), member.userId());
            List<DailyCapacityEntry> entries = computeDailyEntries(context, query.fromDate(), query.toDate(), null);
            BigDecimal totalWorking = sum(entries, DailyCapacityEntry::workingHours);
            BigDecimal totalFocused = sum(entries, DailyCapacityEntry::focusedHours);
            BigDecimal totalAllocationPercent = sumAllocationPercent(
                    allocationRepository.findActiveByUserIdAndDateRange(member.userId(), query.fromDate(), query.toDate()));
            boolean overAllocated = totalAllocationPercent.compareTo(HUNDRED) > 0;
            if (overAllocated) {
                overAllocatedCount++;
            }

            summaries.add(new UserCapacitySummary(
                    member.userId(), member.id(), totalWorking, totalFocused, totalAllocationPercent, overAllocated));
            workspaceFocusedTotal = workspaceFocusedTotal.add(totalFocused);
        }

        return new WorkspaceOverviewResponse(
                query.workspaceId(), query.fromDate(), query.toDate(), summaries, workspaceFocusedTotal, overAllocatedCount);
    }

    @Transactional(readOnly = true)
    public ProjectAllocationSummaryResponse getProjectAllocationSummary(ProjectAllocationSummaryQuery query) {
        authorizationService.requireProjectWorkspacePermission(query.projectId(), IamAuthorities.CAPACITY_VIEW);
        validateRange(query.fromDate(), query.toDate());

        List<ProjectResourceAllocation> allocations = allocationRepository.findActiveByProjectId(query.projectId())
                .stream()
                .filter(allocation -> allocation.overlaps(query.fromDate(), query.toDate()))
                .toList();

        List<AllocationSummaryItem> items = allocations.stream()
                .map(a -> new AllocationSummaryItem(
                        a.id(), a.workspaceMemberId(), a.userId(), a.allocationPercent(),
                        a.allocationType().name(), a.startDate(), a.endDate()))
                .toList();

        BigDecimal totalPercent = sumAllocationPercent(allocations);
        long distinctUsers = allocations.stream().map(ProjectResourceAllocation::userId).distinct().count();

        return new ProjectAllocationSummaryResponse(
                query.projectId(), query.fromDate(), query.toDate(), items, totalPercent, (int) distinctUsers);
    }

    @Transactional(readOnly = true)
    public OverAllocationResponse getOverAllocations(OverAllocationQuery query) {
        authorizationService.requireCapacityView(query.workspaceId());
        validateRange(query.fromDate(), query.toDate());

        List<WorkspaceMember> activeMembers = findAllActiveMembers(query.workspaceId());
        List<OverAllocatedUser> overAllocatedUsers = new ArrayList<>();

        for (WorkspaceMember member : activeMembers) {
            List<ProjectResourceAllocation> overlapping =
                    allocationRepository.findActiveByUserIdAndDateRange(member.userId(), query.fromDate(), query.toDate());
            BigDecimal totalPercent = sumAllocationPercent(overlapping);
            if (totalPercent.compareTo(HUNDRED) > 0) {
                List<UUID> allocationIds = overlapping.stream().map(ProjectResourceAllocation::id).toList();
                overAllocatedUsers.add(new OverAllocatedUser(member.userId(), member.id(), totalPercent, allocationIds));
            }
        }

        return new OverAllocationResponse(query.workspaceId(), query.fromDate(), query.toDate(), overAllocatedUsers);
    }

    @Transactional(readOnly = true)
    public CapacityCalculationResponse calculate(CalculateCapacityQuery query) {
        authorizationService.requireCapacityCalculate(query.workspaceId());
        validateRange(query.fromDate(), query.toDate());

        UserCapacityContext context = resolveContext(query.workspaceId(), query.userId());

        BigDecimal projectPercent = null;
        if (query.projectId() != null) {
            projectPercent = allocationRepository.findActiveByUserIdAndDateRange(query.userId(), query.fromDate(), query.toDate())
                    .stream()
                    .filter(a -> a.projectId().equals(query.projectId()))
                    .map(ProjectResourceAllocation::allocationPercent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        List<DailyCapacityEntry> entries = computeDailyEntries(context, query.fromDate(), query.toDate(), projectPercent);

        BigDecimal totalWorking = sum(entries, DailyCapacityEntry::workingHours);
        BigDecimal totalFocused = sum(entries, DailyCapacityEntry::focusedHours);
        BigDecimal totalProjectAllocated = entries.stream()
                .map(DailyCapacityEntry::projectAllocatedHours)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CapacityCalculationResponse(
                query.workspaceId(), query.userId(), query.projectId(), query.fromDate(), query.toDate(),
                entries, totalWorking, totalFocused,
                query.projectId() != null ? totalProjectAllocated : null);
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            throw CapacityExceptions.calculationInvalidRange();
        }
    }

    private UserCapacityContext resolveContext(UUID workspaceId, UUID userId) {
        Optional<UserCapacityProfile> profile = profileRepository.findActiveByUserId(userId);
        if (profile.isPresent()) {
            UserCapacityProfile p = profile.get();
            return new UserCapacityContext(p.focusFactor(), p.defaultDailyHours(), p.workingCalendarId(), false);
        }

        return workingCalendarRepository.findDefaultActiveByWorkspaceId(workspaceId)
                .map(calendar -> new UserCapacityContext(DEFAULT_FOCUS_FACTOR, DEFAULT_DAILY_HOURS, calendar.id(), true))
                .orElseGet(() -> new UserCapacityContext(DEFAULT_FOCUS_FACTOR, DEFAULT_DAILY_HOURS, null, true));
    }

    private List<DailyCapacityEntry> computeDailyEntries(UserCapacityContext context, LocalDate from, LocalDate to,
                                                          BigDecimal projectAllocationPercent) {
        Map<LocalDate, CalendarException> exceptionsByDate = new HashMap<>();
        Map<DayOfWeek, CalendarDayRule> rulesByDay = new HashMap<>();

        if (context.workingCalendarId() != null) {
            for (CalendarException exception : exceptionRepository
                    .findByWorkingCalendarIdAndDateRange(context.workingCalendarId(), from, to)) {
                exceptionsByDate.put(exception.exceptionDate(), exception);
            }
            for (CalendarDayRule rule : dayRuleRepository.findByWorkingCalendarId(context.workingCalendarId())) {
                rulesByDay.put(rule.dayOfWeek(), rule);
            }
        }

        List<DailyCapacityEntry> entries = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            CalendarException exception = exceptionsByDate.get(date);
            boolean isWorkingDay;
            BigDecimal workingHours;

            if (exception != null) {
                isWorkingDay = exception.isWorkingDay();
                workingHours = exception.workingHours();
            } else {
                CalendarDayRule rule = rulesByDay.get(date.getDayOfWeek());
                if (rule != null) {
                    isWorkingDay = rule.isWorkingDay();
                    workingHours = rule.workingHours();
                } else {
                    boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
                    isWorkingDay = !isWeekend;
                    workingHours = isWeekend ? BigDecimal.ZERO : context.defaultDailyHours();
                }
            }

            BigDecimal focusedHours = workingHours.multiply(context.focusFactor()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal projectAllocatedHours = null;
            if (projectAllocationPercent != null) {
                projectAllocatedHours = focusedHours
                        .multiply(projectAllocationPercent)
                        .divide(HUNDRED, 2, RoundingMode.HALF_UP);
            }

            entries.add(new DailyCapacityEntry(date, isWorkingDay, workingHours, focusedHours, projectAllocatedHours));
        }
        return entries;
    }

    private List<WorkspaceMember> findAllActiveMembers(UUID workspaceId) {
        List<WorkspaceMember> members = new ArrayList<>();
        int page = 0;
        while (true) {
            var pageQuery = PageQuery.of(page, 100);
            var result = workspaceMemberRepository.findAll(workspaceId, null, WorkspaceMemberStatus.ACTIVE, pageQuery);
            members.addAll(result.content());
            if (result.last() || result.content().isEmpty()) {
                break;
            }
            page++;
        }
        return members;
    }

    private BigDecimal sumAllocationPercent(List<ProjectResourceAllocation> allocations) {
        return allocations.stream()
                .map(ProjectResourceAllocation::allocationPercent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(List<DailyCapacityEntry> entries, java.util.function.Function<DailyCapacityEntry, BigDecimal> extractor) {
        return entries.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record UserCapacityContext(
            BigDecimal focusFactor,
            BigDecimal defaultDailyHours,
            UUID workingCalendarId,
            boolean usingLazyDefaults
    ) {}
}
