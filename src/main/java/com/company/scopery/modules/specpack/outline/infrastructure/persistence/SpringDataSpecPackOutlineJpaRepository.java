package com.company.scopery.modules.specpack.outline.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecPackOutlineJpaRepository extends JpaRepository<SpecPackOutlineJpaEntity, UUID> {

    List<SpecPackOutlineJpaEntity> findBySessionId(UUID sessionId);

    List<SpecPackOutlineJpaEntity> findBySessionIdAndStatus(UUID sessionId, String status);

    @Query("SELECT o FROM SpecPackOutlineJpaEntity o WHERE o.sessionId = :sessionId " +
           "AND o.status IN ('DRAFT', 'APPROVED') ORDER BY o.versionNumber DESC LIMIT 1")
    Optional<SpecPackOutlineJpaEntity> findLatestDraftOrApproved(@Param("sessionId") UUID sessionId);
}
