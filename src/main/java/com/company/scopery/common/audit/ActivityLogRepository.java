package com.company.scopery.common.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLogJpaEntity, UUID> {

    @Query("""
            select e from ActivityLogJpaEntity e
            where e.entityId = :projectId
              and e.entityType = :entityType
            order by e.createdAt desc
            """)
    Page<ActivityLogJpaEntity> findProjectActivityFeed(
            @Param("projectId") String projectId,
            @Param("entityType") String entityType,
            Pageable pageable);
}
