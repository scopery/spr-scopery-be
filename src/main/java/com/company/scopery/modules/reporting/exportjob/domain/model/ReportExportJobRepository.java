package com.company.scopery.modules.reporting.exportjob.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ReportExportJobRepository {
    ReportExportJob save(ReportExportJob job);
    Optional<ReportExportJob> findById(UUID id);
    List<ReportExportJob> findByProjectId(UUID projectId);
}
