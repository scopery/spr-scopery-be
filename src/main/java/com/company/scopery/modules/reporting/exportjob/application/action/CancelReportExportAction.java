package com.company.scopery.modules.reporting.exportjob.application.action;

import com.company.scopery.modules.reporting.exportjob.application.command.CancelReportExportCommand;
import com.company.scopery.modules.reporting.exportjob.application.response.ReportExportJobResponse;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportStatus;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJobRepository;
import com.company.scopery.modules.reporting.shared.activity.ReportingActivityLogger;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.constant.ReportingActivityActions;
import com.company.scopery.modules.reporting.shared.constant.ReportingEntityTypes;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelReportExportAction {
    private final ReportExportJobRepository exports;
    private final ReportingAuthorizationService authorization;
    private final ReportingActivityLogger activityLogger;

    public CancelReportExportAction(ReportExportJobRepository exports,
                                    ReportingAuthorizationService authorization,
                                    ReportingActivityLogger activityLogger) {
        this.exports = exports;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ReportExportJobResponse execute(CancelReportExportCommand command) {
        ReportExportJob job = exports.findById(command.exportJobId())
                .orElseThrow(() -> ReportingExceptions.exportNotFound(command.exportJobId()));
        if (job.projectId() != null) {
            authorization.requireExport(job.projectId());
        }
        if (job.status() == ReportExportStatus.CANCELLED) {
            return ReportExportJobResponse.from(job);
        }
        if (!job.isCancellable()) {
            throw ReportingExceptions.exportNotCancellable(job.id());
        }
        ReportExportJob cancelled = exports.save(job.cancel());
        activityLogger.logSuccess(ReportingEntityTypes.EXPORT_JOB, cancelled.id(),
                ReportingActivityActions.REPORT_EXPORT_CANCELLED, "Report export cancelled");
        return ReportExportJobResponse.from(cancelled);
    }
}
