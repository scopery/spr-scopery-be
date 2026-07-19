package com.company.scopery.modules.reporting.exportjob.infrastructure.mapper;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportFormat;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportStatus;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.infrastructure.persistence.ReportExportJobJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReportExportJobPersistenceMapper {
    public ReportExportJob toDomain(ReportExportJobJpaEntity e) {
        return new ReportExportJob(e.getId(), e.getReportRunId(), e.getReportSnapshotId(), e.getReportDefinitionId(),
                e.getWorkspaceId(), e.getProjectId(), e.getActorUserId(), ReportExportFormat.valueOf(e.getFormat()),
                ReportExportStatus.valueOf(e.getStatus()), e.getFileName(), e.getFileMimeType(), e.getFileSizeBytes(),
                e.getStorageKey(), e.getContentText(), e.getDownloadExpiresAt(), e.getFiltersJson(), e.getSelectedFieldsJson(),
                e.getMaskingSummaryJson(), e.getErrorCode(), e.getErrorMessage(), e.getCreatedAt(), e.getCompletedAt(),
                e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion());
    }
    public ReportExportJobJpaEntity toJpaEntity(ReportExportJob d) {
        ReportExportJobJpaEntity e = new ReportExportJobJpaEntity();
        e.setId(d.id()); e.setReportRunId(d.reportRunId()); e.setReportSnapshotId(d.reportSnapshotId());
        e.setReportDefinitionId(d.reportDefinitionId()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setActorUserId(d.actorUserId()); e.setFormat(d.format().name()); e.setStatus(d.status().name());
        e.setFileName(d.fileName()); e.setFileMimeType(d.fileMimeType()); e.setFileSizeBytes(d.fileSizeBytes());
        e.setStorageKey(d.storageKey()); e.setContentText(d.contentText()); e.setDownloadExpiresAt(d.downloadExpiresAt());
        e.setFiltersJson(d.filtersJson()); e.setSelectedFieldsJson(d.selectedFieldsJson());
        e.setMaskingSummaryJson(d.maskingSummaryJson()); e.setErrorCode(d.errorCode()); e.setErrorMessage(d.errorMessage());
        e.setCreatedAt(d.createdAt()); e.setCompletedAt(d.completedAt()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        return e;
    }
}
