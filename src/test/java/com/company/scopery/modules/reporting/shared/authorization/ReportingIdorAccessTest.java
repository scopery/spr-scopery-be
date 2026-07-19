package com.company.scopery.modules.reporting.shared.authorization;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.reporting.exportjob.application.service.ReportExportQueryService;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportFormat;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJobRepository;
import com.company.scopery.modules.reporting.run.application.service.ReportRunQueryService;
import com.company.scopery.modules.reporting.run.domain.enums.ReportRunStatus;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.shared.error.ReportingErrorCatalog;
import com.company.scopery.modules.reporting.snapshot.application.service.ReportSnapshotQueryService;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportingIdorAccessTest {

    @Mock private ProjectWorkspaceAuthorizationService projectAuthorization;
    @Mock private ReportRunRepository runs;
    @Mock private ReportExportJobRepository exports;
    @Mock private ReportSnapshotRepository snapshots;

    private ReportingAuthorizationService authorization;
    private ReportRunQueryService runQueryService;
    private ReportExportQueryService exportQueryService;
    private ReportSnapshotQueryService snapshotQueryService;

    private final UUID foreignProjectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authorization = new ReportingAuthorizationService(projectAuthorization);
        runQueryService = new ReportRunQueryService(runs, authorization);
        exportQueryService = new ReportExportQueryService(exports, authorization);
        snapshotQueryService = new ReportSnapshotQueryService(snapshots, authorization);
    }

    @Test
    void requireReportView_mapsIamAccessDeniedToReportAccessDenied() {
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(projectAuthorization)
                .requireProjectPermission(eq(foreignProjectId), eq(IamAuthorities.REPORTING_REPORT_VIEW));

        assertThatThrownBy(() -> authorization.requireReportView(foreignProjectId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(ReportingErrorCatalog.REPORT_ACCESS_DENIED.code());
                });
    }

    @Test
    void reportRunGet_foreignProject_denied() {
        UUID runId = UUID.randomUUID();
        when(runs.findById(runId)).thenReturn(Optional.of(foreignProjectRun(runId)));
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(projectAuthorization)
                .requireProjectPermission(eq(foreignProjectId), eq(IamAuthorities.REPORTING_REPORT_VIEW));

        assertThatThrownBy(() -> runQueryService.getById(runId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(ReportingErrorCatalog.REPORT_ACCESS_DENIED.code());
                });
    }

    @Test
    void exportDownload_foreignProject_denied() {
        UUID exportJobId = UUID.randomUUID();
        when(exports.findById(exportJobId)).thenReturn(Optional.of(foreignProjectExport(exportJobId)));
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(projectAuthorization)
                .requireProjectPermission(eq(foreignProjectId), eq(IamAuthorities.REPORTING_REPORT_EXPORT));

        assertThatThrownBy(() -> exportQueryService.requireDownloadable(exportJobId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(ReportingErrorCatalog.REPORT_ACCESS_DENIED.code());
                });
    }

    @Test
    void snapshotGet_foreignProject_denied() {
        UUID reportRunId = UUID.randomUUID();
        when(snapshots.findByReportRunId(reportRunId)).thenReturn(Optional.of(foreignProjectSnapshot(reportRunId)));
        doThrow(new AppException(IamErrorCatalog.IAM_ACCESS_DENIED, "Access denied"))
                .when(projectAuthorization)
                .requireProjectPermission(eq(foreignProjectId), eq(IamAuthorities.REPORTING_REPORT_VIEW));

        assertThatThrownBy(() -> snapshotQueryService.getByReportRunId(reportRunId))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(ReportingErrorCatalog.REPORT_ACCESS_DENIED.code());
                });
    }

    private ReportRun foreignProjectRun(UUID runId) {
        Instant now = Instant.now();
        return new ReportRun(
                runId, UUID.randomUUID(), UUID.randomUUID(), foreignProjectId, UUID.randomUUID(),
                ReportRunStatus.COMPLETED, null, null, null, null, "{}", null, null,
                now, now, "trace", 0, now, now);
    }

    private ReportExportJob foreignProjectExport(UUID exportJobId) {
        Instant now = Instant.now();
        return new ReportExportJob(
                exportJobId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), foreignProjectId, UUID.randomUUID(),
                ReportExportFormat.CSV,
                com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportStatus.COMPLETED,
                "report.csv", "text/csv", 12L, "inline:key", "a,b\n1,2",
                now.plusSeconds(3600), null, null, null, null, null, now, now, "trace", 0);
    }

    private ReportSnapshot foreignProjectSnapshot(UUID reportRunId) {
        Instant now = Instant.now();
        return new ReportSnapshot(
                UUID.randomUUID(), reportRunId, UUID.randomUUID(), UUID.randomUUID(), foreignProjectId,
                UUID.randomUUID(), "FULL", "{}", null, null, now, now, 0);
    }
}
