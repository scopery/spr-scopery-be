package com.company.scopery.modules.reporting.snapshot.domain.model;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ReportSnapshotRepository {
    ReportSnapshot save(ReportSnapshot snapshot);
    Optional<ReportSnapshot> findByReportRunId(UUID reportRunId);
    int deleteOlderThan(Instant cutoff);
}
