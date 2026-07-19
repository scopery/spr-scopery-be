package com.company.scopery.modules.profitability.summary.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface SpringDataProfitSnapshotJpaRepository extends JpaRepository<ProfitSnapshotJpaEntity, UUID> {}
