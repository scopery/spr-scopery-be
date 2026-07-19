package com.company.scopery.modules.integrationhub.connection.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataConnectionHealthCheckJpaRepository extends JpaRepository<ConnectionHealthCheckJpaEntity, UUID> {
    List<ConnectionHealthCheckJpaEntity> findByConnectionIdOrderByCheckedAtDesc(UUID connectionId);
    List<ConnectionHealthCheckJpaEntity> findByWorkspaceId(UUID workspaceId);
}
