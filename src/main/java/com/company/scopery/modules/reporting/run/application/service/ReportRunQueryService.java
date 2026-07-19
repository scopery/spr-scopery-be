package com.company.scopery.modules.reporting.run.application.service;

import com.company.scopery.modules.reporting.run.application.response.ReportRunResponse;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReportRunQueryService {
    private final ReportRunRepository runs;
    private final ReportingAuthorizationService authorization;

    public ReportRunQueryService(ReportRunRepository runs,
                                 ReportingAuthorizationService authorization) {
        this.runs = runs;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ReportRunResponse getById(UUID reportRunId) {
        ReportRun run = runs.findById(reportRunId)
                .orElseThrow(() -> ReportingExceptions.runNotFound(reportRunId));
        if (run.projectId() != null) {
            authorization.requireReportView(run.projectId());
        }
        return new ReportRunResponse(
                run.id(),
                run.reportDefinitionId(),
                run.projectId(),
                run.status().name(),
                run.resultSummaryJson(),
                run.maskingSummaryJson(),
                run.completedAt());
    }
}
