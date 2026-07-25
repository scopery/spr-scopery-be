package com.company.scopery.platform.bulkjob.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataBulkJobJpaRepository extends JpaRepository<BulkJobJpaEntity, UUID> {

    @Query(value = """
            SELECT * FROM app_bulk_job
            WHERE status = 'QUEUED'
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<BulkJobJpaEntity> findClaimableWithSkipLocked(@Param("limit") int limit);
}
