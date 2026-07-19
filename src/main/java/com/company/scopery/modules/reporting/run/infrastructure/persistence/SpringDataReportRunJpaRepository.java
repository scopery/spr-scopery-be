package com.company.scopery.modules.reporting.run.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataReportRunJpaRepository extends JpaRepository<ReportRunJpaEntity, UUID> {
    List<ReportRunJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
