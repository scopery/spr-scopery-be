package com.company.scopery.modules.trust.retention.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataRetentionJobJpaRepository extends JpaRepository<RetentionJobJpaEntity, UUID> {
    List<RetentionJobJpaEntity> findByWorkspaceId(UUID workspaceId);
}
