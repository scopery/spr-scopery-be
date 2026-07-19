package com.company.scopery.modules.reporting.definition.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataReportDefinitionJpaRepository extends JpaRepository<ReportDefinitionJpaEntity, UUID> {
    Optional<ReportDefinitionJpaEntity> findByCode(String code);
    List<ReportDefinitionJpaEntity> findByStatusOrderByCodeAsc(String status);
}
