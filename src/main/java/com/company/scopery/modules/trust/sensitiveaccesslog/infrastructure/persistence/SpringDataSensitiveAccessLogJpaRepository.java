package com.company.scopery.modules.trust.sensitiveaccesslog.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSensitiveAccessLogJpaRepository extends JpaRepository<SensitiveAccessLogJpaEntity, UUID> {
    List<SensitiveAccessLogJpaEntity> findByWorkspaceId(UUID workspaceId);
}
