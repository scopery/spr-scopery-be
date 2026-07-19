package com.company.scopery.modules.profitability.summary.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataProjectProfitabilitySummaryJpaRepository extends JpaRepository<ProjectProfitabilitySummaryJpaEntity, UUID> {
    Optional<ProjectProfitabilitySummaryJpaEntity> findByProjectIdAndCurrency(UUID projectId, String currency);
}
