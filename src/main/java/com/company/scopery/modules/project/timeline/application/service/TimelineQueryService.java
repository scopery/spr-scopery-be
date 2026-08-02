package com.company.scopery.modules.project.timeline.application.service;

import com.company.scopery.modules.project.gantt.application.service.GanttTaskDateResolver;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.timeline.application.query.TimelineViewQuery;
import com.company.scopery.modules.project.timeline.application.response.TaskDailyAllocationListResponse;
import com.company.scopery.modules.project.timeline.application.response.TaskDailyAllocationResponse;
import com.company.scopery.modules.project.timeline.application.response.TaskProgressSnapshotListResponse;
import com.company.scopery.modules.project.timeline.application.response.TaskProgressSnapshotResponse;
import com.company.scopery.modules.project.timeline.application.response.TimelineBucketResponse;
import com.company.scopery.modules.project.timeline.application.response.TimelineTaskItemResponse;
import com.company.scopery.modules.project.timeline.application.response.TimelineViewResponse;
import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocation;
import com.company.scopery.modules.project.timeline.domain.model.TaskDailyAllocationRepository;
import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshot;
import com.company.scopery.modules.project.timeline.domain.model.TaskProgressSnapshotRepository;
import com.company.scopery.modules.project.timeline.domain.rules.TimelineBucketCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TimelineQueryService {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final ScheduleRunRepository runs;
    private final TaskScheduleRepository schedules;
    private final TaskScheduleOverrideRepository overrides;
    private final TaskDailyAllocationRepository allocations;
    private final TaskProgressSnapshotRepository snapshots;

    public TimelineQueryService(
            ProjectWorkspaceAuthorizationService authorization,
            ProjectRepository projects,
            TaskRepository tasks,
            ScheduleRunRepository runs,
            TaskScheduleRepository schedules,
            TaskScheduleOverrideRepository overrides,
            TaskDailyAllocationRepository allocations,
            TaskProgressSnapshotRepository snapshots) {
        this.authorization = authorization;
        this.projects = projects;
        this.tasks = tasks;
        this.runs = runs;
        this.schedules = schedules;
        this.overrides = overrides;
        this.allocations = allocations;
        this.snapshots = snapshots;
    }

    @Transactional(readOnly = true)
    public TimelineViewResponse getView(TimelineViewQuery query) {
        authorization.requireGanttView(query.projectId());
        Project project = projects.findById(query.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(query.projectId()));

        if (query.from() == null || query.to() == null || query.to().isBefore(query.from())) {
            throw ProjectExceptions.timelineInvalidDateRange();
        }
        if (query.granularity() == null) {
            throw ProjectExceptions.timelineInvalidGranularity();
        }

        List<Task> taskList = tasks.findAllByProjectId(project.id()).stream()
                .filter(t -> t.status() != TaskStatus.ARCHIVED)
                .toList();

        Map<UUID, TaskSchedule> scheduleByTask = resolveSchedules(project);
        Map<UUID, TaskScheduleOverride> overrideByTask = overrides.findActiveByProjectId(project.id()).stream()
                .collect(Collectors.toMap(TaskScheduleOverride::taskId, o -> o, (a, b) -> a));

        Map<UUID, Map<LocalDate, Integer>> manualByTask = new HashMap<>();
        for (TaskDailyAllocation row : allocations.findByProjectId(project.id())) {
            if (row.source() != AllocationSource.MANUAL) {
                continue;
            }
            manualByTask
                    .computeIfAbsent(row.taskId(), ignored -> new HashMap<>())
                    .put(row.workDate(), row.plannedMinutes());
        }

        Map<UUID, List<TimelineBucketCalculator.ProgressPoint>> snapshotsByTask = new HashMap<>();
        for (TaskProgressSnapshot snap : snapshots.findByProjectId(project.id())) {
            snapshotsByTask
                    .computeIfAbsent(snap.taskId(), ignored -> new ArrayList<>())
                    .add(new TimelineBucketCalculator.ProgressPoint(snap.snapshotDate(), snap.progressPercent()));
        }

        List<TimelineBucketCalculator.Period> periods = TimelineBucketCalculator.buildPeriods(
                query.from(), query.to(), query.granularity());

        LocalDate today = LocalDate.now();
        List<TimelineTaskItemResponse> items = new ArrayList<>(taskList.size());
        for (Task task : taskList) {
            GanttTaskDateResolver.EffectiveTaskDates dates = GanttTaskDateResolver.resolve(
                    task,
                    scheduleByTask.get(task.id()),
                    overrideByTask.get(task.id()));

            Integer estimateMinutes = toEstimateMinutes(task.estimateHours());
            Map<LocalDate, Integer> daily = TimelineBucketCalculator.resolveDailyAllocationMinutes(
                    dates.start(),
                    dates.end(),
                    estimateMinutes,
                    manualByTask.get(task.id()));

            List<TimelineBucketCalculator.BucketMetrics> buckets = TimelineBucketCalculator.buildBuckets(
                    periods,
                    dates.start(),
                    dates.end(),
                    estimateMinutes,
                    daily,
                    snapshotsByTask.getOrDefault(task.id(), List.of()));

            NavigableMap<LocalDate, BigDecimal> progressIndex = new TreeMap<>();
            for (TimelineBucketCalculator.ProgressPoint point
                    : snapshotsByTask.getOrDefault(task.id(), List.of())) {
                progressIndex.put(point.snapshotDate(), point.progressPercent());
            }
            BigDecimal headProgress = TimelineBucketCalculator.resolveActualAsOf(progressIndex, today);

            items.add(new TimelineTaskItemResponse(
                    task.id(),
                    estimateMinutes,
                    headProgress,
                    dates.start(),
                    dates.end(),
                    buckets.stream().map(this::toBucketResponse).toList()));
        }

        return new TimelineViewResponse(items);
    }

    @Transactional(readOnly = true)
    public TaskProgressSnapshotListResponse listProjectSnapshots(UUID projectId) {
        authorization.requireGanttView(projectId);
        projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return new TaskProgressSnapshotListResponse(
                snapshots.findByProjectId(projectId).stream().map(this::toSnapshotResponse).toList());
    }

    @Transactional(readOnly = true)
    public TaskProgressSnapshotListResponse listTaskSnapshots(UUID projectId, UUID taskId) {
        authorization.requireGanttView(projectId);
        requireTaskInProject(projectId, taskId);
        return new TaskProgressSnapshotListResponse(
                snapshots.findByTaskId(taskId).stream().map(this::toSnapshotResponse).toList());
    }

    @Transactional(readOnly = true)
    public TaskDailyAllocationListResponse listProjectAllocations(UUID projectId) {
        authorization.requireGanttView(projectId);
        projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return new TaskDailyAllocationListResponse(
                allocations.findByProjectId(projectId).stream()
                        .map(a -> new TaskDailyAllocationResponse(
                                a.id(), a.taskId(), a.workDate(), a.plannedMinutes(), a.source()))
                        .toList());
    }

    @Transactional(readOnly = true)
    public TaskDailyAllocationListResponse listTaskAllocations(UUID projectId, UUID taskId) {
        authorization.requireGanttView(projectId);
        requireTaskInProject(projectId, taskId);
        return new TaskDailyAllocationListResponse(
                allocations.findByTaskId(taskId).stream()
                        .map(a -> new TaskDailyAllocationResponse(
                                a.id(), a.taskId(), a.workDate(), a.plannedMinutes(), a.source()))
                        .toList());
    }

    private Map<UUID, TaskSchedule> resolveSchedules(Project project) {
        Map<UUID, TaskSchedule> scheduleByTask = new HashMap<>();
        ScheduleRun run = resolveScheduleRun(project);
        if (run == null) {
            return scheduleByTask;
        }
        schedules.findAllByScheduleRunId(run.id()).forEach(s -> scheduleByTask.put(s.taskId(), s));
        return scheduleByTask;
    }

    private ScheduleRun resolveScheduleRun(Project project) {
        if (project.currentScheduleRunId() != null) {
            return runs.findById(project.currentScheduleRunId())
                    .filter(r -> r.projectId().equals(project.id()))
                    .orElse(null);
        }
        return runs.findAllByProjectId(project.id()).stream()
                .filter(r -> r.status() == ScheduleRunStatus.COMPLETED)
                .findFirst()
                .orElse(null);
    }

    private Task requireTaskInProject(UUID projectId, UUID taskId) {
        projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        Task task = tasks.findById(taskId).orElseThrow(() -> ProjectExceptions.taskNotFound(taskId));
        if (!task.projectId().equals(projectId)) {
            throw ProjectExceptions.taskProjectMismatch(taskId, projectId);
        }
        return task;
    }

    private TimelineBucketResponse toBucketResponse(TimelineBucketCalculator.BucketMetrics metrics) {
        return new TimelineBucketResponse(
                metrics.periodStart(),
                metrics.periodEnd(),
                metrics.plannedMinutes(),
                metrics.plannedContributionPercent(),
                metrics.cumulativePlannedPercent(),
                metrics.actualProgressPercent(),
                metrics.variancePercent());
    }

    private TaskProgressSnapshotResponse toSnapshotResponse(TaskProgressSnapshot snap) {
        return new TaskProgressSnapshotResponse(
                snap.id(),
                snap.projectId(),
                snap.taskId(),
                snap.snapshotDate(),
                snap.progressPercent(),
                snap.timeSpentMinutes(),
                snap.note(),
                snap.recordedBy(),
                snap.recordedAt());
    }

    private static Integer toEstimateMinutes(BigDecimal estimateHours) {
        if (estimateHours == null || estimateHours.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return estimateHours.multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
