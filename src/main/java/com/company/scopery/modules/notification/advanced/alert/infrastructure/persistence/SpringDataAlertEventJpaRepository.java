package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataAlertEventJpaRepository extends JpaRepository<AlertEventJpaEntity, UUID> {
    Optional<AlertEventJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<AlertEventJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
