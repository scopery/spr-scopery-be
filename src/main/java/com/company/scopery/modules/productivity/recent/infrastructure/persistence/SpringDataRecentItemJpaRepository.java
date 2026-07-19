package com.company.scopery.modules.productivity.recent.infrastructure.persistence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataRecentItemJpaRepository extends JpaRepository<RecentItemJpaEntity, UUID> {
    List<RecentItemJpaEntity> findByUserIdOrderByViewedAtDesc(UUID userId, Pageable pageable);
}
