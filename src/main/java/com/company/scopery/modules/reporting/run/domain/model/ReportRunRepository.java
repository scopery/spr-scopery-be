package com.company.scopery.modules.reporting.run.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ReportRunRepository {
    ReportRun save(ReportRun run);
    Optional<ReportRun> findById(UUID id);
    List<ReportRun> findByProjectId(UUID projectId);
}
