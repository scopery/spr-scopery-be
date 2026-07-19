package com.company.scopery.modules.reporting.exportjob.domain.model;

import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportFormat;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportExportJobLifecycleTest {

    @Test
    void completedExport_isDownloadable_andNotCancellable() {
        ReportExportJob job = ReportExportJob.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                ReportExportFormat.CSV, "report.csv", "a,b\n1,2", "{}", "trace");

        assertThat(job.status()).isEqualTo(ReportExportStatus.COMPLETED);
        assertThat(job.isDownloadable()).isTrue();
        assertThat(job.isCancellable()).isFalse();
        assertThatThrownBy(job::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void pendingExport_canBeCancelled() {
        ReportExportJob completed = ReportExportJob.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                ReportExportFormat.JSON, "report.json", "{}", "{}", "trace");
        ReportExportJob pending = new ReportExportJob(
                completed.id(), completed.reportRunId(), completed.reportSnapshotId(), completed.reportDefinitionId(),
                completed.workspaceId(), completed.projectId(), completed.actorUserId(),
                completed.format(), ReportExportStatus.PENDING, completed.fileName(), completed.fileMimeType(),
                completed.fileSizeBytes(), completed.storageKey(), completed.contentText(), completed.downloadExpiresAt(),
                completed.filtersJson(), completed.selectedFieldsJson(), completed.maskingSummaryJson(),
                null, null, completed.createdAt(), null, completed.traceId(), 0);

        assertThat(pending.isCancellable()).isTrue();
        ReportExportJob cancelled = pending.cancel();
        assertThat(cancelled.status()).isEqualTo(ReportExportStatus.CANCELLED);
        assertThat(cancelled.isDownloadable()).isFalse();
    }
}
