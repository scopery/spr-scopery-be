package com.company.scopery.modules.project.scheduling.schedulerun.application.service;

import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.application.response.ScheduledDailyWorkResponse;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.ScheduledDailyWork;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.ScheduledDailyWorkRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.schedulingissue.application.response.SchedulingIssueResponse;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueSeverity;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueType;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssue;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssueRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.application.response.TaskScheduleResponse;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleQueryService {

    private final ProjectRepository projects;
    private final ScheduleRunRepository runs;
    private final TaskScheduleRepository schedules;
    private final ScheduledDailyWorkRepository work;
    private final SchedulingIssueRepository issues;
    private final ProjectWorkspaceAuthorizationService authorization;

    public ScheduleQueryService(ProjectRepository projects,
                                ScheduleRunRepository runs,
                                TaskScheduleRepository schedules,
                                ScheduledDailyWorkRepository work,
                                SchedulingIssueRepository issues,
                                ProjectWorkspaceAuthorizationService authorization) {
        this.projects = projects;
        this.runs = runs;
        this.schedules = schedules;
        this.work = work;
        this.issues = issues;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ScheduleRunResponse> listRuns(UUID projectId) {
        authorization.requireScheduleView(projectId);
        return runs.findAllByProjectId(projectId).stream().map(ScheduleRunResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleRunResponse getRun(UUID projectId, UUID id) {
        authorization.requireScheduleView(projectId);
        return ScheduleRunResponse.from(requireRun(projectId, id));
    }

    @Transactional(readOnly = true)
    public ScheduleRunResponse current(UUID projectId) {
        return ScheduleRunResponse.from(requireCurrent(projectId));
    }

    @Transactional(readOnly = true)
    public List<TaskScheduleResponse> currentTasks(UUID projectId,
                                                   UUID taskId,
                                                   UUID assigneeUserId,
                                                   String riskStatus,
                                                   String scheduleStatus) {
        ScheduleRun run = requireCurrent(projectId);
        TaskScheduleRiskStatus risk = ProjectEnumParser.parseOptional(
                TaskScheduleRiskStatus.class, riskStatus, "INVALID_RISK_STATUS", "riskStatus");
        TaskScheduleStatus status = ProjectEnumParser.parseOptional(
                TaskScheduleStatus.class, scheduleStatus, "INVALID_SCHEDULE_STATUS", "scheduleStatus");
        return schedules.findAllByScheduleRunId(run.id()).stream()
                .filter(s -> taskId == null || taskId.equals(s.taskId()))
                .filter(s -> assigneeUserId == null || assigneeUserId.equals(s.assigneeUserId()))
                .filter(s -> risk == null || risk == s.riskStatus())
                .filter(s -> status == null || status == s.scheduleStatus())
                .map(TaskScheduleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduledDailyWorkResponse> currentWork(UUID projectId,
                                                        UUID taskId,
                                                        UUID assigneeUserId,
                                                        LocalDate dateFrom,
                                                        LocalDate dateTo) {
        ScheduleRun run = requireCurrent(projectId);
        List<ScheduledDailyWork> rows = (dateFrom != null && dateTo != null)
                ? work.findAllByScheduleRunIdAndDateRange(run.id(), dateFrom, dateTo)
                : work.findAllByScheduleRunId(run.id());
        return rows.stream()
                .filter(w -> taskId == null || taskId.equals(w.taskId()))
                .filter(w -> assigneeUserId == null || assigneeUserId.equals(w.userId()))
                .filter(w -> dateFrom == null || !w.workDate().isBefore(dateFrom))
                .filter(w -> dateTo == null || !w.workDate().isAfter(dateTo))
                .map(ScheduledDailyWorkResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SchedulingIssueResponse> currentIssues(UUID projectId,
                                                       UUID taskId,
                                                       String issueType,
                                                       String severity) {
        ScheduleRun run = requireCurrent(projectId);
        SchedulingIssueType type = ProjectEnumParser.parseOptional(
                SchedulingIssueType.class, issueType, "INVALID_ISSUE_TYPE", "issueType");
        SchedulingIssueSeverity sev = ProjectEnumParser.parseOptional(
                SchedulingIssueSeverity.class, severity, "INVALID_ISSUE_SEVERITY", "severity");
        return issues.findAllByScheduleRunId(run.id()).stream()
                .filter(i -> taskId == null || taskId.equals(i.taskId()))
                .filter(i -> type == null || type == i.issueType())
                .filter(i -> sev == null || sev == i.severity())
                .map(SchedulingIssueResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskScheduleResponse taskCurrent(UUID projectId, UUID taskId) {
        ScheduleRun run = requireCurrent(projectId);
        return TaskScheduleResponse.from(schedules.findByScheduleRunIdAndTaskId(run.id(), taskId)
                .orElseThrow(() -> ProjectExceptions.taskScheduleNotFound(taskId)));
    }

    @Transactional(readOnly = true)
    public List<TaskScheduleResponse> taskHistory(UUID projectId, UUID taskId) {
        authorization.requireScheduleView(projectId);
        return schedules.findHistory(projectId, taskId).stream().map(TaskScheduleResponse::from).toList();
    }

    private ScheduleRun requireCurrent(UUID projectId) {
        authorization.requireScheduleView(projectId);
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return runs.findCurrent(projectId, project.currentScheduleRunId())
                .orElseThrow(() -> ProjectExceptions.scheduleRunNotFound(project.currentScheduleRunId()));
    }

    private ScheduleRun requireRun(UUID projectId, UUID id) {
        return runs.findById(id)
                .filter(r -> r.projectId().equals(projectId))
                .orElseThrow(() -> ProjectExceptions.scheduleRunNotFound(id));
    }
}
