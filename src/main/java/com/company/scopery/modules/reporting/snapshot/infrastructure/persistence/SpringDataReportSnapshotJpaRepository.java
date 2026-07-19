package com.company.scopery.modules.reporting.snapshot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataReportSnapshotJpaRepository extends JpaRepository<ReportSnapshotJpaEntity, UUID> {
    Optional<ReportSnapshotJpaEntity> findByReportRunId(UUID reportRunId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ReportSnapshotJpaEntity s where s.generatedAt < :cutoff")
    int deleteByGeneratedAtBefore(@Param("cutoff") Instant cutoff);
}
