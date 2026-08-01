package com.company.scopery.modules.project.mywork.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.mywork.application.query.MyWorkQuery;
import com.company.scopery.modules.project.mywork.application.response.MyWorkResponse;
import com.company.scopery.modules.project.mywork.application.response.MyWorkResponse.MyWorkPageInfo;
import com.company.scopery.modules.project.mywork.application.response.MyWorkSummary;
import com.company.scopery.modules.project.mywork.application.response.MyWorkTaskItem;
import com.company.scopery.modules.project.mywork.domain.enums.MyWorkWindow;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MyWorkQueryService {

    private static final List<String> TERMINAL = List.of("DONE", "CANCELLED", "ARCHIVED");

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectPhaseRepository phaseRepository;

    public MyWorkQueryService(CurrentUserAuthorizationService currentUserService,
                              WorkspaceIamIntegrationService iamIntegrationService,
                              ProjectRepository projectRepository,
                              TaskRepository taskRepository,
                              ProjectPhaseRepository phaseRepository) {
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.phaseRepository = phaseRepository;
    }

    @Transactional(readOnly = true)
    public MyWorkResponse query(MyWorkQuery q) {
        IamUser user = currentUserService.resolveCurrentUser();
        iamIntegrationService.requireWorkspaceAccess(q.workspaceId(), user.id(), IamAuthorities.PROJECT_VIEW);

        List<Project> projects = projectRepository.findAllByWorkspaceId(q.workspaceId()).stream()
                .filter(p -> iamIntegrationService.canViewProject(
                        p.id(), q.workspaceId(), user.id(), IamAuthorities.PROJECT_VIEW))
                .toList();
        if (q.projectId() != null) {
            projects = projects.stream().filter(p -> p.id().equals(q.projectId())).toList();
        }
        Map<UUID, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::id, p -> p));
        Collection<UUID> projectIds = projectMap.keySet();

        if (projectIds.isEmpty()) {
            return emptyResponse(q.workspaceId(), user.id(), q);
        }

        LocalDate today = LocalDate.now();
        WindowDates wd = resolveWindow(q, today);

        List<String> statuses = (q.statuses() == null || q.statuses().isEmpty()) ? null : q.statuses();
        // OVERDUE window always excludes terminal statuses regardless of includeCompleted flag
        boolean excludeTerminal = q.window() == MyWorkWindow.OVERDUE || !q.includeCompleted();

        PageQuery pageQuery = PageQuery.of(q.page(), Math.min(q.size(), 100));
        PageResult<Task> result = taskRepository.searchForUser(
                projectIds, user.id(),
                statuses, excludeTerminal,
                wd.dateFrom(), wd.dateTo(),
                wd.dueDateOnly(), wd.includeUndated(),
                pageQuery);

        // Batch-load phase names
        Set<UUID> phaseIds = result.content().stream()
                .map(Task::projectPhaseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, ProjectPhase> phaseMap = phaseRepository.findAllByIdIn(phaseIds)
                .stream().collect(Collectors.toMap(ProjectPhase::id, ph -> ph));

        List<MyWorkTaskItem> items = result.content().stream()
                .map(t -> toItem(t, projectMap.get(t.projectId()), phaseMap.get(t.projectPhaseId()), today))
                .toList();

        MyWorkSummary summary = computeSummary(projectIds, user.id(), result.totalElements(),
                today, wd.dateFrom(), wd.dateTo(), excludeTerminal);

        return new MyWorkResponse(
                q.workspaceId(), user.id(),
                q.window(), wd.dateFrom(), wd.dateTo(),
                summary, items,
                new MyWorkPageInfo(result.page(), result.size(), result.totalElements(), result.totalPages()));
    }

    // ─── Window resolution ────────────────────────────────────────────────────

    private record WindowDates(LocalDate dateFrom, LocalDate dateTo, boolean dueDateOnly, boolean includeUndated) {}

    private WindowDates resolveWindow(MyWorkQuery q, LocalDate today) {
        return switch (q.window()) {
            case THIS_WEEK -> {
                // LocalDate.with(DayOfWeek) advances forward — wrong for Sat/Sun.
                LocalDate from = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate to = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                yield new WindowDates(from, to, false, false);
            }
            case OVERDUE -> new WindowDates(
                    null,
                    today.minusDays(1),
                    true, false);
            case UPCOMING -> new WindowDates(
                    today,
                    today.plusDays(13),
                    false, false);
            case ALL_OPEN -> new WindowDates(null, null, false, true);
            case CUSTOM -> new WindowDates(q.dateFrom(), q.dateTo(), false, false);
        };
    }

    // ─── Summary ──────────────────────────────────────────────────────────────

    private MyWorkSummary computeSummary(Collection<UUID> projectIds, UUID userId, long total,
                                         LocalDate today, LocalDate dateFrom, LocalDate dateTo,
                                         boolean excludeTerminal) {

        long inProgress = taskRepository.countForUser(projectIds, userId,
                List.of("IN_PROGRESS"), false, null, null, false, true);
        long todo = taskRepository.countForUser(projectIds, userId,
                List.of("TODO"), false, null, null, false, true);
        long blocked = taskRepository.countForUser(projectIds, userId,
                List.of("BLOCKED"), false, null, null, false, true);

        // Overdue: dueDate <= yesterday, exclude terminal
        long overdue = taskRepository.countForUser(projectIds, userId,
                null, true, null, today.minusDays(1), true, false);

        // Due this window: dueDate in [dateFrom, dateTo], exclude terminal
        long dueThisWindow = (dateFrom != null && dateTo != null)
                ? taskRepository.countForUser(projectIds, userId, null, true, dateFrom, dateTo, true, false)
                : 0L;

        long undated = taskRepository.countUndatedForUser(projectIds, userId, excludeTerminal);

        return new MyWorkSummary(total, overdue, dueThisWindow, inProgress, todo, blocked, undated);
    }

    // ─── Item mapping ─────────────────────────────────────────────────────────

    private MyWorkTaskItem toItem(Task t, Project project, ProjectPhase phase, LocalDate today) {
        boolean isOverdue = t.dueDate() != null
                && t.dueDate().isBefore(today)
                && !TERMINAL.contains(t.status().name());
        return new MyWorkTaskItem(
                t.id(),
                t.projectId(),
                project != null ? project.code() : null,
                project != null ? project.name() : null,
                t.code(),
                t.title(),
                t.status().name(),
                t.priority().name(),
                t.inChargeUserId(),
                t.plannedStartDate(),
                t.dueDate(),
                t.estimateHours(),
                t.projectPhaseId(),
                phase != null ? phase.name() : null,
                t.wbsNodeId(),
                isOverdue,
                t.updatedAt(),
                t.completedAt(),
                t.completedBy());
    }

    // ─── Empty response ───────────────────────────────────────────────────────

    private MyWorkResponse emptyResponse(UUID workspaceId, UUID userId, MyWorkQuery q) {
        LocalDate today = LocalDate.now();
        WindowDates wd = resolveWindow(q, today);
        MyWorkSummary emptySummary = new MyWorkSummary(0, 0, 0, 0, 0, 0, 0);
        return new MyWorkResponse(workspaceId, userId, q.window(), wd.dateFrom(), wd.dateTo(),
                emptySummary, List.of(), new MyWorkPageInfo(0, q.size(), 0, 0));
    }
}
