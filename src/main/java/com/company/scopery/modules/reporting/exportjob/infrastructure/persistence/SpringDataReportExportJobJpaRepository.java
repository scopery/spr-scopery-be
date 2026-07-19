package com.company.scopery.modules.reporting.exportjob.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataReportExportJobJpaRepository extends JpaRepository<ReportExportJobJpaEntity, UUID> {
    List<ReportExportJobJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
