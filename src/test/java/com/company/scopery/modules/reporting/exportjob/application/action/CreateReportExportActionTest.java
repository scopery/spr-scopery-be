package com.company.scopery.modules.reporting.exportjob.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
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
import com.company.scopery.modules.reporting.shared.error.ReportingErrorCatalog;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReportExportActionTest {

    @Mock ReportRunRepository runs;
    @Mock ReportSnapshotRepository snapshots;
    @Mock ReportExportJobRepository exports;
    @Mock ReportingAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock ReportingActivityLogger activityLogger;
    @Mock IamUser iamUser;

    private CreateReportExportAction action;
    private final UUID runId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateReportExportAction(runs, snapshots, exports, authorization, currentUser,
                activityLogger, new ObjectMapper());
    }

    @Test
    void exportCsvValidSuccess() {
        when(runs.findById(runId)).thenReturn(Optional.of(completedRun()));
        when(snapshots.findByReportRunId(runId)).thenReturn(Optional.of(snapshot()));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(exports.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateReportExportResponse response = action.execute(
                new CreateReportExportCommand(runId, "CSV", "task-risk.csv"));

        assertThat(response.format()).isEqualTo(ReportExportFormat.CSV.name());
        assertThat(response.fileName()).isEqualTo("task-risk.csv");
        assertThat(response.downloadAvailable()).isTrue();

        ArgumentCaptor<ReportExportJob> captor = ArgumentCaptor.forClass(ReportExportJob.class);
        verify(exports).save(captor.capture());
        assertThat(captor.getValue().contentText()).contains("totalTasks,10");
        assertThat(captor.getValue().contentText()).contains("overdueTasks,3");
        assertThat(captor.getValue().contentText()).doesNotContain("report,{");
    }

    @Test
    void exportUnsupportedFormatRejected() {
        when(runs.findById(runId)).thenReturn(Optional.of(completedRun()));

        assertThatThrownBy(() -> action.execute(new CreateReportExportCommand(runId, "PDF", null)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_EXPORT_FORMAT_NOT_SUPPORTED.code()));
    }

    @Test
    void exportUsesMaskedSnapshotContent() {
        when(runs.findById(runId)).thenReturn(Optional.of(completedRun()));
        when(snapshots.findByReportRunId(runId)).thenReturn(Optional.of(
                ReportSnapshot.create(runId, UUID.randomUUID(), UUID.randomUUID(), projectId, UUID.randomUUID(),
                        "FINANCE", "{\"plannedRevenue\":\"REDACTED\"}", "{}", "{\"finance\":\"REDACTED\"}")));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(exports.save(any())).thenAnswer(inv -> inv.getArgument(0));

        action.execute(new CreateReportExportCommand(runId, "CSV", null));

        ArgumentCaptor<ReportExportJob> captor = ArgumentCaptor.forClass(ReportExportJob.class);
        verify(exports).save(captor.capture());
        assertThat(captor.getValue().contentText()).contains("plannedRevenue,REDACTED");
        assertThat(captor.getValue().maskingSummaryJson()).contains("REDACTED");
    }

    private ReportRun completedRun() {
        Instant now = Instant.now();
        return new ReportRun(runId, UUID.randomUUID(), UUID.randomUUID(), projectId, UUID.randomUUID(),
                ReportRunStatus.COMPLETED, "{}", "[]", "{}", "{}", "{}", null, null,
                now, now, "trace", 0, now, now);
    }

    private ReportSnapshot snapshot() {
        return ReportSnapshot.create(runId, UUID.randomUUID(), UUID.randomUUID(), projectId, UUID.randomUUID(),
                "TASK_RISK", "{\"totalTasks\":10,\"overdueTasks\":3}", "{}", "{}");
    }
}
