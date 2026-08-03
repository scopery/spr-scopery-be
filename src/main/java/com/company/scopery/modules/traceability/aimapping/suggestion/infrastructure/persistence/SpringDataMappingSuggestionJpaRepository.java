package com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SpringDataMappingSuggestionJpaRepository
        extends JpaRepository<MappingSuggestionJpaEntity, UUID>,
                JpaSpecificationExecutor<MappingSuggestionJpaEntity> {

    List<MappingSuggestionJpaEntity> findByRunIdAndReviewStatus(UUID runId, String reviewStatus);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MappingSuggestionJpaEntity e
            SET e.reviewStatus = :reviewStatus,
                e.reviewedBy = :reviewedBy,
                e.reviewedAt = :reviewedAt,
                e.updatedAt = CURRENT_TIMESTAMP
            WHERE e.id = :id
            """)
    int updateReviewStatus(@Param("id") UUID id,
                           @Param("reviewStatus") String reviewStatus,
                           @Param("reviewedBy") UUID reviewedBy,
                           @Param("reviewedAt") Instant reviewedAt);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE MappingSuggestionJpaEntity e
            SET e.reviewStatus = :reviewStatus,
                e.reviewedBy = :reviewedBy,
                e.reviewedAt = :reviewedAt,
                e.updatedAt = CURRENT_TIMESTAMP
            WHERE e.id IN :ids
            """)
    int bulkUpdateReviewStatus(@Param("ids") List<UUID> ids,
                               @Param("reviewStatus") String reviewStatus,
                               @Param("reviewedBy") UUID reviewedBy,
                               @Param("reviewedAt") Instant reviewedAt);
}
