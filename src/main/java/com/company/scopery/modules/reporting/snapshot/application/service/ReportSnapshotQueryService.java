package com.company.scopery.modules.reporting.snapshot.application.service;

import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingExceptions;
import com.company.scopery.modules.reporting.snapshot.application.response.ReportSnapshotResponse;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReportSnapshotQueryService {
    private final ReportSnapshotRepository snapshots;
    private final ReportingAuthorizationService authorization;

    public ReportSnapshotQueryService(ReportSnapshotRepository snapshots,
                                      ReportingAuthorizationService authorization) {
        this.snapshots = snapshots;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ReportSnapshotResponse getByReportRunId(UUID reportRunId) {
        ReportSnapshot snap = snapshots.findByReportRunId(reportRunId)
                .orElseThrow(() -> ReportingExceptions.snapshotNotFound(reportRunId));
        if (snap.projectId() != null) {
            authorization.requireReportView(snap.projectId());
        }
        return new ReportSnapshotResponse(
                snap.id(),
                snap.reportRunId(),
                snap.snapshotType(),
                snap.dataJson(),
                snap.maskingSummaryJson());
    }
}
