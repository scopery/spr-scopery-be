package com.company.scopery.modules.reporting.exportjob.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.reporting.exportjob.application.command.CreateReportExportCommand;
import com.company.scopery.modules.reporting.exportjob.application.response.CreateReportExportResponse;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportFormat;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJobRepository;
import com.company.scopery.modules.reporting.run.domain.enums.ReportRunStatus;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.shared.activity.ReportingActivityLogger;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.constant.ReportingActivityActions;
import com.company.scopery.modules.reporting.shared.constant.ReportingEntityTypes;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import com.company.scopery.modules.reporting.shared.export.CsvReportSerializer;
import com.company.scopery.modules.reporting.shared.export.XlsxReportSerializer;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateReportExportAction {
    private final ReportRunRepository runs;
    private final ReportSnapshotRepository snapshots;
    private final ReportExportJobRepository exports;
    private final ReportingAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ReportingActivityLogger activityLogger;
    private final CsvReportSerializer csvSerializer;
    private final XlsxReportSerializer xlsxSerializer;

    public CreateReportExportAction(ReportRunRepository runs, ReportSnapshotRepository snapshots,
                                    ReportExportJobRepository exports,
                                    ReportingAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    ReportingActivityLogger activityLogger,
                                    ObjectMapper objectMapper) {
        this.runs = runs;
        this.snapshots = snapshots;
        this.exports = exports;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
        this.csvSerializer = new CsvReportSerializer(objectMapper);
        this.xlsxSerializer = new XlsxReportSerializer(objectMapper);
    }

    @Transactional
    public CreateReportExportResponse execute(CreateReportExportCommand command) {
        UUID reportRunId = command.reportRunId();
        ReportRun run = runs.findById(reportRunId).orElseThrow(() -> ReportingExceptions.runNotFound(reportRunId));
        if (run.projectId() != null) {
            authorization.requireExport(run.projectId());
        }
        if (run.status() != ReportRunStatus.COMPLETED) {
            throw ReportingExceptions.runNotCompleted(reportRunId);
        }
        ReportExportFormat exportFormat;
        try {
            exportFormat = ReportExportFormat.valueOf(
                    command.format() == null ? "CSV" : command.format().trim().toUpperCase());
        } catch (Exception ex) {
            throw ReportingExceptions.invalidFormat(command.format());
        }
        ReportSnapshot snapshot = snapshots.findByReportRunId(reportRunId)
                .orElseThrow(() -> ReportingExceptions.snapshotNotFound(reportRunId));
        var actor = currentUser.resolveCurrentUser();
        String content = switch (exportFormat) {
            case CSV -> csvSerializer.toCsv(snapshot.dataJson());
            case XLSX -> xlsxSerializer.toXlsxBase64(snapshot.dataJson());
            default -> snapshot.dataJson();
        };
        String name = command.fileName() == null || command.fileName().isBlank()
                ? "report-" + reportRunId + switch (exportFormat) {
                    case CSV -> ".csv";
                    case XLSX -> ".xlsx";
                    default -> ".json";
                }
                : command.fileName();
        ReportExportJob job = exports.save(ReportExportJob.create(
                run.id(), snapshot.id(), run.reportDefinitionId(), run.workspaceId(), run.projectId(),
                actor.id(), exportFormat, name, content, snapshot.maskingSummaryJson(), MDC.get("traceId")));
        activityLogger.logSuccess(ReportingEntityTypes.EXPORT_JOB, job.id(),
                ReportingActivityActions.REPORT_EXPORT_CREATED, "Report export created");
        return new CreateReportExportResponse(
                job.id(), job.status().name(), job.format().name(), job.fileName(), true);
    }
}
