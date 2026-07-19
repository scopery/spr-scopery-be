package com.company.scopery.modules.reporting.run.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.reporting.dashboard.application.service.ProjectDashboardQueryService;
import com.company.scopery.modules.reporting.definition.domain.enums.ReportDefinitionStatus;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinitionRepository;
import com.company.scopery.modules.reporting.run.application.command.CreateReportRunCommand;
import com.company.scopery.modules.reporting.run.application.response.CreateReportRunResponse;
import com.company.scopery.modules.reporting.run.application.response.ReportMaskingSummaryResponse;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.shared.activity.ReportingActivityLogger;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.constant.ReportingActivityActions;
import com.company.scopery.modules.reporting.shared.constant.ReportingEntityTypes;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class CreateReportRunAction {
    private final ReportDefinitionRepository definitions;
    private final ReportRunRepository runs;
    private final ReportSnapshotRepository snapshots;
    private final ProjectRepository projects;
    private final ProjectDashboardQueryService dashboard;
    private final ReportingAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ReportingActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CreateReportRunAction(ReportDefinitionRepository definitions, ReportRunRepository runs,
                                 ReportSnapshotRepository snapshots, ProjectRepository projects,
                                 ProjectDashboardQueryService dashboard,
                                 ReportingAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser,
                                 ReportingActivityLogger activityLogger,
                                 ObjectMapper objectMapper) {
        this.definitions = definitions;
        this.runs = runs;
        this.snapshots = snapshots;
        this.projects = projects;
        this.dashboard = dashboard;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CreateReportRunResponse execute(CreateReportRunCommand command) {
        if (command.projectId() == null) {
            throw ReportingExceptions.projectRequired();
        }
        authorization.requireReportRun(command.projectId());
        ReportDefinition definition = definitions.findByCode(command.reportCode())
                .orElseThrow(() -> ReportingExceptions.definitionNotFound(command.reportCode()));
        if (definition.status() == ReportDefinitionStatus.DEPRECATED
                || definition.status() == ReportDefinitionStatus.ARCHIVED) {
            throw ReportingExceptions.definitionDeprecated(definition.code());
        }
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        var actor = currentUser.resolveCurrentUser();

        ReportRun run = ReportRun.create(definition.id(), project.workspaceId(), command.projectId(), actor.id(),
                json(command.filters()), json(command.selectedFields()), MDC.get("traceId"));
        run = runs.save(run.markRunning());

        Map<String, Object> data = dashboard.resolveReportData(definition.reportType(), command.projectId());
        ReportMaskingSummaryResponse masking = new ReportMaskingSummaryResponse(
                authorization.canViewFinance(command.projectId()) ? null : "REDACTED",
                authorization.canViewQuote(command.projectId()) ? null : "REDACTED");

        ReportSnapshot snapshot = snapshots.save(ReportSnapshot.create(
                run.id(), definition.id(), project.workspaceId(), command.projectId(), actor.id(),
                definition.reportType(), json(data), json(Map.of("reportCode", command.reportCode())), json(masking)));
        run = runs.save(run.markCompleted(json(Map.of("actorUserId", actor.id())), json(masking),
                json(Map.of("snapshotId", snapshot.id(), "keys", data.keySet()))));
        activityLogger.logSuccess(ReportingEntityTypes.RUN, run.id(),
                ReportingActivityActions.REPORT_RUN_COMPLETED, "Report run completed: " + command.reportCode());

        return new CreateReportRunResponse(
                run.id(), run.status().name(), snapshot.id(), objectMapper.valueToTree(data), masking);
    }

    private String json(Object o) {
        try {
            return objectMapper.writeValueAsString(o == null ? Map.of() : o);
        } catch (Exception e) {
            return "{}";
        }
    }
}
