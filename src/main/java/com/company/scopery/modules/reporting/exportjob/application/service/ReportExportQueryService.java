package com.company.scopery.modules.reporting.exportjob.application.service;

import com.company.scopery.modules.reporting.exportjob.application.response.ReportExportJobResponse;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJobRepository;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReportExportQueryService {
    private final ReportExportJobRepository exports;
    private final ReportingAuthorizationService authorization;

    public ReportExportQueryService(ReportExportJobRepository exports,
                                    ReportingAuthorizationService authorization) {
        this.exports = exports;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ReportExportJobResponse> listByProject(UUID projectId) {
        authorization.requireExport(projectId);
        return exports.findByProjectId(projectId).stream().map(ReportExportJobResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ReportExportJobResponse getById(UUID exportJobId) {
        ReportExportJob job = findAuthorized(exportJobId);
        return ReportExportJobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public ReportExportJob requireDownloadable(UUID exportJobId) {
        ReportExportJob job = findAuthorized(exportJobId);
        if (job.downloadExpiresAt() != null && job.downloadExpiresAt().isBefore(Instant.now())) {
            throw ReportingExceptions.exportExpired(exportJobId);
        }
        if (!job.isDownloadable()) {
            throw ReportingExceptions.exportNotReady(exportJobId);
        }
        return job;
    }

    private ReportExportJob findAuthorized(UUID exportJobId) {
        ReportExportJob job = exports.findById(exportJobId)
                .orElseThrow(() -> ReportingExceptions.exportNotFound(exportJobId));
        if (job.projectId() != null) {
            authorization.requireExport(job.projectId());
        }
        return job;
    }
}
